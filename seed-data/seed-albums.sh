#!/usr/bin/env bash

set -euo pipefail

# Script hangi klasörden çalıştırılırsa çalıştırılsın,
# kendi bulunduğu klasörü temel alır.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

ALBUMS_API_URL="${ALBUMS_API_URL:-http://localhost:8081/api/v1/albums}"
ARTISTS_API_URL="${ARTISTS_API_URL:-http://localhost:8081/api/v1/artists}"

JSON_FILE="${JSON_FILE:-$SCRIPT_DIR/albums.json}"

# Albüm ve artist görselleri script ile aynı klasörde duruyor.
IMAGES_DIR="${IMAGES_DIR:-$SCRIPT_DIR/images}"

ADMIN_TOKEN="${ADMIN_TOKEN:-}"

SUCCESS_COUNT=0
FAILED_COUNT=0
SKIPPED_COUNT=0

ARTISTS_RESPONSE=""


print_info() {
  echo "[INFO] $1"
}


print_success() {
  echo "[SUCCESS] $1"
}


print_warning() {
  echo "[WARNING] $1"
}


print_error() {
  echo "[ERROR] $1" >&2
}


check_requirements() {
  if ! command -v curl >/dev/null 2>&1; then
    print_error "curl bulunamadı."
    exit 1
  fi

  if ! command -v jq >/dev/null 2>&1; then
    print_error "jq bulunamadı."
    print_error "Fedora kurulumu: sudo dnf install jq"
    exit 1
  fi

  if [[ ! -f "$JSON_FILE" ]]; then
    print_error "JSON dosyası bulunamadı: $JSON_FILE"
    exit 1
  fi

  if ! jq empty "$JSON_FILE" >/dev/null 2>&1; then
    print_error "Geçersiz JSON dosyası: $JSON_FILE"
    exit 1
  fi

  if ! jq -e 'type == "array"' "$JSON_FILE" >/dev/null 2>&1; then
    print_error "albums.json dosyasının kök değeri bir JSON dizisi olmalıdır."
    exit 1
  fi

  if [[ ! -d "$IMAGES_DIR" ]]; then
    print_error "Görsel dizini bulunamadı: $IMAGES_DIR"
    exit 1
  fi

  if [[ -z "$ADMIN_TOKEN" ]]; then
    print_error "ADMIN_TOKEN ortam değişkeni tanımlı değil."
    print_error "Örnek: ADMIN_TOKEN='token' ./seed-albums.sh"
    exit 1
  fi
}


fetch_artists() {
  local response_file
  local http_status

  response_file="$(mktemp)"

  print_info "Sanatçı listesi alınıyor: $ARTISTS_API_URL"

  http_status="$(
    curl \
      --silent \
      --show-error \
      --output "$response_file" \
      --write-out "%{http_code}" \
      --request GET "$ARTISTS_API_URL" \
      --header "Authorization: Bearer $ADMIN_TOKEN"
  )"

  ARTISTS_RESPONSE="$(cat "$response_file")"
  rm -f "$response_file"

  if [[ "$http_status" != "200" ]]; then
    print_error "Sanatçı listesi alınamadı."
    print_error "HTTP status: $http_status"
    print_error "Response: $ARTISTS_RESPONSE"
    exit 1
  fi

  if ! jq empty <<< "$ARTISTS_RESPONSE" >/dev/null 2>&1; then
    print_error "Artists endpoint geçerli JSON döndürmedi."
    print_error "Response: $ARTISTS_RESPONSE"
    exit 1
  fi

  if ! jq -e 'type == "array"' <<< "$ARTISTS_RESPONSE" >/dev/null 2>&1; then
    print_error "Artists endpoint bir JSON dizisi döndürmelidir."
    exit 1
  fi

  local artist_count
  artist_count="$(jq 'length' <<< "$ARTISTS_RESPONSE")"

  print_success "Sanatçı listesi alındı. Toplam: $artist_count"
}


find_artist_id() {
  local artist_name="$1"
  local artist_id

  artist_id="$(
    jq -r \
      --arg artistName "$artist_name" \
      '
        [
          .[]
          | select(
              (.name | ascii_downcase)
              == ($artistName | ascii_downcase)
            )
          | .id
        ]
        | first // empty
      ' <<< "$ARTISTS_RESPONSE"
  )"

  if [[ -z "$artist_id" || "$artist_id" == "null" ]]; then
    return 1
  fi

  printf '%s\n' "$artist_id"
}


seed_album() {
  local title="$1"
  local release_year="$2"
  local artist_name="$3"
  local cover_image_name="$4"

  local artist_id
  local cover_image_path
  local response_file
  local response_body
  local http_status

  echo
  print_info "Albüm işleniyor: $title"

  if [[ -z "$title" ]]; then
    print_error "Albüm adı boş. Kayıt atlandı."
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$release_year" ]]; then
    print_error "Yayın yılı boş. Albüm atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ ! "$release_year" =~ ^[0-9]{4}$ ]]; then
    print_error "Geçersiz yayın yılı: $release_year"
    print_error "Albüm atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$artist_name" ]]; then
    print_error "Sanatçı adı boş. Albüm atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$cover_image_name" ]]; then
    print_error "Albüm kapağı belirtilmemiş: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if ! artist_id="$(find_artist_id "$artist_name")"; then
    print_error "Sanatçı bulunamadı: $artist_name"
    print_error "Albüm atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  cover_image_path="${IMAGES_DIR}/${cover_image_name}"

  if [[ ! -f "$cover_image_path" ]]; then
    print_error "Albüm kapağı bulunamadı: $cover_image_path"
    print_error "Albüm atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  print_info "Sanatçı: $artist_name"
  print_info "Sanatçı ID: $artist_id"
  print_info "Yayın yılı: $release_year"
  print_info "Kapak: $cover_image_path"

  response_file="$(mktemp)"

  http_status="$(
    curl \
      --silent \
      --show-error \
      --output "$response_file" \
      --write-out "%{http_code}" \
      --request POST "$ALBUMS_API_URL" \
      --header "Authorization: Bearer $ADMIN_TOKEN" \
      --form-string "title=$title" \
      --form-string "releaseYear=$release_year" \
      --form-string "artistId=$artist_id" \
      --form "image=@${cover_image_path}"
  )"

  response_body="$(cat "$response_file")"
  rm -f "$response_file"

  case "$http_status" in
    200|201)
      print_success "Albüm eklendi: $title"

      if jq empty <<< "$response_body" >/dev/null 2>&1; then
        jq . <<< "$response_body"
      else
        echo "$response_body"
      fi

      SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
      ;;

    409)
      print_warning "Albüm zaten mevcut olabilir: $title"
      echo "$response_body"
      SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
      ;;

    401|403)
      print_error "Yetkilendirme başarısız."
      print_error "ADMIN_TOKEN geçerli ve ADMIN rolüne ait olmalıdır."
      print_error "HTTP status: $http_status"
      print_error "Response: $response_body"
      FAILED_COUNT=$((FAILED_COUNT + 1))
      ;;

    *)
      print_error "Albüm eklenemedi: $title"
      print_error "HTTP status: $http_status"
      print_error "Response: $response_body"
      FAILED_COUNT=$((FAILED_COUNT + 1))
      ;;
  esac
}


main() {
  local album_count
  local index

  local title
  local release_year
  local artist_name
  local cover_image

  check_requirements
  fetch_artists

  album_count="$(jq 'length' "$JSON_FILE")"

  echo
  print_info "Toplam albüm sayısı: $album_count"
  print_info "Albums endpoint: $ALBUMS_API_URL"
  print_info "JSON dosyası: $JSON_FILE"
  print_info "Görsel dizini: $IMAGES_DIR"

  for ((index = 0; index < album_count; index++)); do
    title="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].title // empty' \
        "$JSON_FILE"
    )"

    release_year="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].releaseYear // empty' \
        "$JSON_FILE"
    )"

    artist_name="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].artistName // empty' \
        "$JSON_FILE"
    )"

    cover_image="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].coverImage // empty' \
        "$JSON_FILE"
    )"

    seed_album \
      "$title" \
      "$release_year" \
      "$artist_name" \
      "$cover_image"
  done

  echo
  echo "----------------------------------------"
  echo "Albüm seed işlemi tamamlandı."
  echo "Başarılı : $SUCCESS_COUNT"
  echo "Başarısız: $FAILED_COUNT"
  echo "Atlanan  : $SKIPPED_COUNT"
  echo "----------------------------------------"

  if [[ "$FAILED_COUNT" -gt 0 ]]; then
    exit 1
  fi
}


main "$@"
