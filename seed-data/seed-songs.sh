#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

SONGS_API_URL="${SONGS_API_URL:-http://localhost:8081/api/v1/songs}"
ALBUMS_API_URL="${ALBUMS_API_URL:-http://localhost:8081/api/v1/albums}"

JSON_FILE="${JSON_FILE:-$SCRIPT_DIR/songs.json}"
AUDIO_DIR="${AUDIO_DIR:-$SCRIPT_DIR/audio}"

ADMIN_TOKEN="${ADMIN_TOKEN:-}"

# Backend DTO alan adları farklıysa ortam değişkenleriyle değiştirebilirsin.
TITLE_FORM_FIELD="${TITLE_FORM_FIELD:-title}"
DURATION_FORM_FIELD="${DURATION_FORM_FIELD:-duration}"
ALBUM_ID_FORM_FIELD="${ALBUM_ID_FORM_FIELD:-albumId}"
AUDIO_FORM_FIELD="${AUDIO_FORM_FIELD:-audioFile}"

SUCCESS_COUNT=0
FAILED_COUNT=0
SKIPPED_COUNT=0

ALBUMS_RESPONSE=""


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
    print_error "songs.json dosyasının kök değeri bir JSON dizisi olmalıdır."
    exit 1
  fi

  if [[ ! -d "$AUDIO_DIR" ]]; then
    print_error "Ses dosyası klasörü bulunamadı: $AUDIO_DIR"
    exit 1
  fi

  if [[ -z "$ADMIN_TOKEN" ]]; then
    print_error "ADMIN_TOKEN ortam değişkeni tanımlı değil."
    print_error "Örnek: ADMIN_TOKEN='token' ./seed-songs.sh"
    exit 1
  fi
}


fetch_albums() {
  local response_file
  local http_status

  response_file="$(mktemp)"

  if ! http_status="$(
    curl \
      --silent \
      --show-error \
      --output "$response_file" \
      --write-out "%{http_code}" \
      --request GET "$ALBUMS_API_URL" \
      --header "Authorization: Bearer $ADMIN_TOKEN"
  )"; then
    rm -f "$response_file"
    print_error "Albums endpoint'e bağlanılamadı: $ALBUMS_API_URL"
    exit 1
  fi

  ALBUMS_RESPONSE="$(cat "$response_file")"
  rm -f "$response_file"

  if [[ "$http_status" != "200" ]]; then
    print_error "Albüm listesi alınamadı."
    print_error "HTTP status: $http_status"
    print_error "Response: $ALBUMS_RESPONSE"
    exit 1
  fi

  if ! jq empty <<< "$ALBUMS_RESPONSE" >/dev/null 2>&1; then
    print_error "Albums endpoint geçerli JSON döndürmedi."
    print_error "Response: $ALBUMS_RESPONSE"
    exit 1
  fi

  if ! jq -e 'type == "array"' <<< "$ALBUMS_RESPONSE" >/dev/null 2>&1; then
    print_error "Albums endpoint bir JSON dizisi döndürmelidir."
    exit 1
  fi

  local album_count
  album_count="$(jq 'length' <<< "$ALBUMS_RESPONSE")"

  print_success "Albüm listesi alındı. Toplam: $album_count"
}


find_album_id() {
  local album_title="$1"
  local artist_name="$2"
  local album_id

  album_id="$(
    jq -r \
      --arg albumTitle "$album_title" \
      --arg artistName "$artist_name" \
      '
        [
          .[]
          | select(
              (.title | ascii_downcase)
              == ($albumTitle | ascii_downcase)
            )
          | select(
              (.artistName | ascii_downcase)
              == ($artistName | ascii_downcase)
            )
          | .id
        ]
        | first // empty
      ' <<< "$ALBUMS_RESPONSE"
  )"

  if [[ -z "$album_id" || "$album_id" == "null" ]]; then
    return 1
  fi

  printf '%s\n' "$album_id"
}


seed_song() {
  local title="$1"
  local duration="$2"
  local album_title="$3"
  local artist_name="$4"
  local audio_file_name="$5"

  local album_id
  local audio_file_path
  local response_file
  local response_body
  local http_status

  echo
  print_info "Şarkı işleniyor: $title"

  if [[ -z "$title" ]]; then
    print_error "Şarkı adı boş. Kayıt atlandı."
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$duration" || ! "$duration" =~ ^[0-9]+$ ]]; then
    print_error "Geçersiz şarkı süresi: $duration"
    print_error "Şarkı atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if (( duration <= 0 )); then
    print_error "Şarkı süresi sıfırdan büyük olmalıdır: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$album_title" ]]; then
    print_error "Albüm adı boş. Şarkı atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$artist_name" ]]; then
    print_error "Sanatçı adı boş. Şarkı atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if [[ -z "$audio_file_name" ]]; then
    print_error "Ses dosyası belirtilmemiş: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  if ! album_id="$(find_album_id "$album_title" "$artist_name")"; then
    print_error "Albüm bulunamadı: $album_title"
    print_error "Sanatçı: $artist_name"
    print_error "Şarkı atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  audio_file_path="${AUDIO_DIR}/${audio_file_name}"

  if [[ ! -f "$audio_file_path" ]]; then
    print_error "Ses dosyası bulunamadı: $audio_file_path"
    print_error "Şarkı atlandı: $title"
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  print_info "Albüm: $album_title"
  print_info "Sanatçı: $artist_name"
  print_info "Albüm ID: $album_id"
  print_info "Süre: $duration saniye"
  print_info "Ses dosyası: $audio_file_path"

  response_file="$(mktemp)"

  if ! http_status="$(
    curl \
      --silent \
      --show-error \
      --output "$response_file" \
      --write-out "%{http_code}" \
      --request POST "$SONGS_API_URL" \
      --header "Authorization: Bearer $ADMIN_TOKEN" \
      --form-string "${TITLE_FORM_FIELD}=${title}" \
      --form-string "${DURATION_FORM_FIELD}=${duration}" \
      --form-string "${ALBUM_ID_FORM_FIELD}=${album_id}" \
      --form "${AUDIO_FORM_FIELD}=@${audio_file_path}"
  )"; then
    rm -f "$response_file"
    print_error "Şarkı isteği gönderilemedi: $title"
    FAILED_COUNT=$((FAILED_COUNT + 1))
    return
  fi

  response_body="$(cat "$response_file")"
  rm -f "$response_file"

  case "$http_status" in
    200|201)
      print_success "Şarkı eklendi: $title"

      if jq empty <<< "$response_body" >/dev/null 2>&1; then
        jq . <<< "$response_body"
      else
        echo "$response_body"
      fi

      SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
      ;;

    409)
      print_warning "Şarkı zaten mevcut olabilir: $title"
      echo "$response_body"
      SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
      ;;

    401|403)
      print_error "Yetkilendirme başarısız: $title"
      print_error "ADMIN_TOKEN geçerli ve ADMIN rolüne ait olmalıdır."
      print_error "HTTP status: $http_status"
      print_error "Response: $response_body"
      FAILED_COUNT=$((FAILED_COUNT + 1))
      ;;

    *)
      print_error "Şarkı eklenemedi: $title"
      print_error "HTTP status: $http_status"
      print_error "Response: $response_body"
      FAILED_COUNT=$((FAILED_COUNT + 1))
      ;;
  esac
}


main() {
  local song_count
  local index

  local title
  local duration
  local album_title
  local artist_name
  local audio_file

  check_requirements
  fetch_albums

  song_count="$(jq 'length' "$JSON_FILE")"

  echo
  print_info "Toplam şarkı sayısı: $song_count"
  print_info "Songs endpoint: $SONGS_API_URL"
  print_info "Albums endpoint: $ALBUMS_API_URL"
  print_info "JSON dosyası: $JSON_FILE"
  print_info "Ses dosyası dizini: $AUDIO_DIR"

  for ((index = 0; index < song_count; index++)); do
    title="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].title // empty' \
        "$JSON_FILE"
    )"

    duration="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].duration // empty' \
        "$JSON_FILE"
    )"

    album_title="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].albumTitle // empty' \
        "$JSON_FILE"
    )"

    artist_name="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].artistName // empty' \
        "$JSON_FILE"
    )"

    audio_file="$(
      jq -r \
        --argjson index "$index" \
        '.[$index].audioFile // empty' \
        "$JSON_FILE"
    )"

    seed_song \
      "$title" \
      "$duration" \
      "$album_title" \
      "$artist_name" \
      "$audio_file"
  done

  echo
  echo "----------------------------------------"
  echo "Şarkı seed işlemi tamamlandı."
  echo "Başarılı : $SUCCESS_COUNT"
  echo "Başarısız: $FAILED_COUNT"
  echo "Atlanan  : $SKIPPED_COUNT"
  echo "----------------------------------------"

  if [[ "$FAILED_COUNT" -gt 0 ]]; then
    exit 1
  fi
}


main "$@"
