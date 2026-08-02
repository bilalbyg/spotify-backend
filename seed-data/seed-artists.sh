#!/usr/bin/env bash

set -euo pipefail

API_URL="${API_URL:-http://localhost:8081/api/v1/artists}"
JSON_FILE="${JSON_FILE:-./artists.json}"
IMAGES_DIR="${IMAGES_DIR:-./images}"
ADMIN_TOKEN="${ADMIN_TOKEN:-}"

SUCCESS_COUNT=0
FAILED_COUNT=0
SKIPPED_COUNT=0

print_error() {
  echo "[ERROR] $1" >&2
}

print_info() {
  echo "[INFO] $1"
}

print_success() {
  echo "[SUCCESS] $1"
}

check_requirements() {
  if ! command -v curl >/dev/null 2>&1; then
    print_error "curl bulunamadı."
    exit 1
  fi

  if ! command -v jq >/dev/null 2>&1; then
    print_error "jq bulunamadı."
    print_error "Fedora için: sudo dnf install jq"
    exit 1
  fi

  if [[ ! -f "$JSON_FILE" ]]; then
    print_error "JSON dosyası bulunamadı: $JSON_FILE"
    exit 1
  fi

  if [[ ! -d "$IMAGES_DIR" ]]; then
    print_error "Resim klasörü bulunamadı: $IMAGES_DIR"
    exit 1
  fi

  if ! jq empty "$JSON_FILE" >/dev/null 2>&1; then
    print_error "Geçersiz JSON dosyası: $JSON_FILE"
    exit 1
  fi

  if [[ -z "$ADMIN_TOKEN" ]]; then
    print_error "ADMIN_TOKEN ortam değişkeni tanımlı değil."
    print_error "Örnek:"
    print_error "export ADMIN_TOKEN='eyJhbGciOiJIUzI1NiJ9...'"
    exit 1
  fi
}

check_api() {
  local health_url

  health_url="${API_URL%/api/v1/artists}"

  print_info "Catalog service kontrol ediliyor: $health_url"

  if ! curl --silent --output /dev/null --connect-timeout 3 "$health_url"; then
    print_info "Ana adres doğrudan cevap vermedi. Artist endpoint üzerinden devam edilecek."
  fi
}

seed_artist() {
  local name="$1"
  local bio="$2"
  local image_name="$3"
  local image_path
  local response_file
  local http_status
  local response_body

  if [[ -z "$name" || "$name" == "null" ]]; then
    print_error "Artist adı boş. Kayıt atlandı."
    SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
    return
  fi

  image_path="${IMAGES_DIR}/${image_name}"

  if [[ -z "$image_name" || "$image_name" == "null" ]]; then
    print_info "Görselsiz artist ekleniyor: $name"

    response_file="$(mktemp)"

    http_status="$(
      curl --silent \
        --show-error \
        --output "$response_file" \
        --write-out "%{http_code}" \
        --request POST "$API_URL" \
        --header "Authorization: Bearer $ADMIN_TOKEN" \
        --form-string "name=$name" \
        --form-string "bio=$bio"
    )"
  else
    if [[ ! -f "$image_path" ]]; then
      print_error "Resim bulunamadı: $image_path"
      print_error "Artist atlandı: $name"
      SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
      return
    fi

    print_info "Artist ekleniyor: $name"
    print_info "Resim: $image_path"

    response_file="$(mktemp)"

    http_status="$(
      curl --silent \
        --show-error \
        --output "$response_file" \
        --write-out "%{http_code}" \
        --request POST "$API_URL" \
        --header "Authorization: Bearer $ADMIN_TOKEN" \
        --form-string "name=$name" \
        --form-string "bio=$bio" \
        --form "image=@${image_path}"
    )"
  fi

  response_body="$(cat "$response_file")"
  rm -f "$response_file"

  case "$http_status" in
    200|201)
      print_success "Artist eklendi: $name"
      echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
      SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
      ;;
    409)
      print_info "Artist zaten mevcut olabilir: $name"
      echo "$response_body"
      SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
      ;;
    *)
      print_error "Artist eklenemedi: $name"
      print_error "HTTP status: $http_status"
      print_error "Response: $response_body"
      FAILED_COUNT=$((FAILED_COUNT + 1))
      ;;
  esac

  echo
}

main() {
  check_requirements
  check_api

  local artist_count
  local index
  local name
  local bio
  local image

  artist_count="$(jq 'length' "$JSON_FILE")"

  print_info "Toplam artist sayısı: $artist_count"
  print_info "Endpoint: $API_URL"
  echo

  for ((index = 0; index < artist_count; index++)); do
    name="$(jq -r ".[$index].name // empty" "$JSON_FILE")"
    bio="$(jq -r ".[$index].bio // empty" "$JSON_FILE")"
    image="$(jq -r ".[$index].image // empty" "$JSON_FILE")"

    seed_artist "$name" "$bio" "$image"
  done

  echo "----------------------------------------"
  echo "Seed işlemi tamamlandı."
  echo "Başarılı : $SUCCESS_COUNT"
  echo "Başarısız: $FAILED_COUNT"
  echo "Atlanan  : $SKIPPED_COUNT"
  echo "----------------------------------------"

  if [[ "$FAILED_COUNT" -gt 0 ]]; then
    exit 1
  fi
}

main "$@"
