# TODO

## Security / Audit

- `@AdminOnly` anotasyonu için AOP tabanlı audit log ekle.
- `@AfterReturning` ile başarılı çağrılarda şu pattern ile log bas:
  `Kullanıcı adı ADMIN yetkisi ile metot ismi metodunu çalıştırdı @ timestamp`
- Log alanları:
  - kullanıcı bilgisi (`SecurityContextHolder -> Authentication -> principal`)
  - metot adı (`JoinPoint`)
  - zaman damgası (`OffsetDateTime`)
- Opsiyonel: `AccessDeniedException` için `@AfterThrowing` ekleyip yetkisiz denemeleri ayrı logla.
- `generateApiKey` işini ekle (güvenli key üretimi, saklama/maskeleme ve rotasyon stratejisi ile birlikte).
- Sistem ayağa kalkar kalkmaz default bir admin hesabı oluşturma işini ekle; en güvenli yöntemle (idempotent seed, güçlü şifre politikası, secret/env üzerinden başlangıç şifresi, ilk girişte şifre değişimi zorunluluğu) uygulanmalı.

## Authentication / Account Management

- Access token + refresh token mimarisine geç. ++
- Refresh token rotation ve revoke desteği ekle. ++
- Logout akışını ekle; oturum bazlı token iptali yap. ++
- E-posta doğrulama akışı ekle.
- `forgot password` ve `reset password` akışlarını ekle.
- Giriş yapan kullanıcı için `change password` endpoint'i ekle.
- Google, Apple veya GitHub ile OAuth login desteği ekle.
- İsteğe bağlı iki faktörlü kimlik doğrulama (2FA/MFA) ekle.
- Aktif oturumlar ve cihaz yönetimi ekle.
- Login için rate limiting ve brute force koruması ekle.

## Catalog Service

### Database / Infrastructure

- Catalog service için ayrı PostgreSQL container'a geç.
  - Mevcut ortak `postgres` container kullanımını bırak.
  - Yeni container adı: `spotify-catalog-postgresql`.
  - DB adı: `spotify_catalog`.
  - DB user: `spotify_catalog_user`.
  - Host port: `5433`.
- `catalog-service/src/main/resources/application.yml` içindeki datasource ayarlarını yeni catalog DB'ye göre güncelle.
  - Local run: `jdbc:postgresql://localhost:5433/spotify_catalog`
  - Docker network run: `jdbc:postgresql://spotify-catalog-postgresql:5432/spotify_catalog`
- Catalog service için DB config değerlerini env değişkenlerine taşı.
  - `CATALOG_DB_URL`
  - `CATALOG_DB_USERNAME`
  - `CATALOG_DB_PASSWORD`
- Catalog service ayağa kalkınca sadece kendi tablolarını yeni `spotify_catalog` DB içinde oluşturduğunu doğrula.
- Eski ortak DB'den gelen test verilerini dikkate alma; catalog datasını sıfırdan seed/test requestleriyle oluştur.

### Domain Boundary Cleanup

- `Playlist` modelinin catalog-service içinde kalıp kalmayacağını netleştir.
  - Şu an `Playlist` içinde `ownerId` alanı auth-service kullanıcısına referans veriyor ve playlist-song ilişkisini catalog içinde tutuyor.
  - Kullanıcıya ait playlist, liked songs ve takip edilen içerikler ileride `library-service` tarafına taşınmalı.
  - Catalog-service içinde playlist kalacaksa sadece sistem/public playlist metadata'sı olarak sınırlandır.
- Catalog-service sorumluluğunu şu domainlerle sınırla:
  - `Artist`
  - `Album`
  - `Song`
  - `Genre`
  - `Lyrics`
  - `Podcast`
  - `Episode`
- Kullanıcıya özel state tutan alanları catalog-service'ten ayır.
  - `ownerId`
  - user playlist ownership
  - liked/favorite/follow ilişkileri

### Song Flow

- `SongController` endpoint path'lerini catalog prefix ile uyumlu hale getir.
  - Mevcut yapı: `/api/v1/songs`
  - Hedef yapı: `/api/v1/catalog/songs`
- `createSong` akışını netleştir.
  - Audio file zorunlu olmaya devam edecek mi?
  - Cover image song seviyesinde mi, album seviyesinde mi tutulacak?
  - Duration backend'e request ile mi gelecek, yoksa upload edilen dosyadan mı hesaplanacak?
- `Song` entity'sinde `audioUrl` dışında şu alanlara ihtiyaç var mı değerlendir:
  - `trackNumber`
  - `explicit`
  - `playCount`
  - `coverImageUrl`
  - `releaseDate`
- `SongServiceImpl.createSong()` içinde sadece album set ediliyor; `genres` ve `collaboratingArtists` alanlarının request'ten alınarak entity'ye bağlanmasını ekle.
- `SongResponse` içine genre ve collaborating artist bilgilerini eklemeyi değerlendir.
- `getSongsByArtist` şu an album artist üzerinden çalışıyor; collaborating artist ilişkisi de dikkate alınacak mı karar ver.
- `searchSongs` endpoint'inde pagination desteği ekle.
  - Şu an `List<SongResponse>` dönüyor.
  - Hedef: `Page<SongResponse>` veya custom paged response.
- `SongSearchCriteria` içinde bulunan `excludePlaylistId` alanını tekrar değerlendir.
  - Bu alan catalog-service yerine library-service/playlist domain'e ait olabilir.
- `RuntimeException("Ses dosyası yüklemek zorunludur!")` gibi ham exceptionları custom exception'a taşı.

### Artist Flow

- `Artist` için gerçek Spotify clone ihtiyacına göre alanları gözden geçir.
  - name
  - biography
  - imageUrl
  - monthlyListeners
  - verified
  - genres
- Artist oluştururken aynı isimde kayıt açılmasını nasıl yöneteceğini belirle.
  - Tamamen unique mi olacak?
  - Aynı isimli farklı artist desteklenecek mi?
- Artist detay response'una artist albumleri ve popüler şarkıları eklenecek mi karar ver.
- Artist image upload akışı MinIO ile desteklenecekse `FileStorageService` üzerinden ekle.
- Artist endpointlerini `/api/v1/catalog/artists` altında standardize et.

### Album Flow

- Album oluştururken artist zorunluluğunu netleştir.
- Album cover upload akışını ekle veya mevcutsa doğrula.
- Album içinde `releaseDate`, `albumType`, `coverImageUrl` gibi alanları netleştir.
- Album detail response'una album şarkılarını ekle.
- Album silme durumunda bağlı song kayıtları için davranışı belirle.
  - Silmeyi engelle.
  - Cascade sil.
  - Soft delete uygula.
- Album endpointlerini `/api/v1/catalog/albums` altında standardize et.

### Genre Flow

- Genre için duplicate name kontrolü ekle.
- Genre name'i case-insensitive unique olacak mı belirle.
  - `Pop`, `pop`, `POP` aynı genre kabul edilmeli.
- Genre silme durumunda bağlı song kayıtları varsa silmeyi engelle.
- Genre listesi public endpoint olarak kalmalı.

### Lyrics Flow

- Lyrics'in `Song` ile birebir ilişkisini doğrula.
- Aynı song için birden fazla lyrics kaydı oluşmasını engelle.
- Lyrics response içinde song bilgisi ne kadar dönecek netleştir.
- Lyrics update endpoint'i ekle.
- Lyrics text uzunluğu için validation ekle.

### Podcast / Episode Flow

- Podcast ve episode gerçekten MVP'de olacak mı karar ver.
- İlk Spotify clone MVP'si sadece music catalog olacaksa `Podcast` ve `Episode` akışlarını TODO'da ikinci faza al.
- Podcast kalacaksa episode upload/stream URL yapısını song ile benzer hale getir.
- Episode duration, releaseDate ve audioUrl alanlarını song yapısıyla tutarlı hale getir.

### File / MinIO

- `FileStorageService` kullanımını song, album cover, artist image ve podcast episode için ortak hale getir.
- MinIO bucket yoksa startup sırasında otomatik oluşturma desteği ekle.
- Upload edilen dosya adlarında çakışma olmaması için UUID tabanlı object key üret.
- Dosya path standardını belirle:
  - `songs/{songId}/audio-{uuid}.mp3`
  - `albums/{albumId}/cover-{uuid}.jpg`
  - `artists/{artistId}/image-{uuid}.jpg`
  - `podcasts/{podcastId}/episodes/{episodeId}/audio-{uuid}.mp3`
- DB içinde full public URL yerine object key mi tutulacak karar ver.
- Dosya upload başarılı olup DB save başarısız olursa MinIO'daki orphan file nasıl temizlenecek belirle.
- DB save başarılı olup MinIO upload başarısız olursa transaction davranışını netleştir.

### Security / Authorization

- Public catalog endpointleri ile admin endpointlerini ayır.
  - Public: get/list/search/detail
  - Admin: create/update/delete/upload
- `@AdminOnly` anotasyonunun catalog-service içinde gerçekten çalıştığını doğrula.
- Catalog-service JWT secret'ının auth-service ile aynı config'ten geldiğini doğrula.
- `/api/v1/catalog/songs`, `/albums`, `/artists`, `/genres` GET endpointleri token istememeli mi karar ver.
- Admin endpointler için 401/403 response formatını standartlaştır.
- Catalog-service içinde auth DB'ye erişim olmamalı; sadece JWT claim üzerinden role kontrolü yapılmalı.

### API Design Cleanup

- Tüm endpointleri tek prefix altında topla:
  - `/api/v1/catalog/songs`
  - `/api/v1/catalog/albums`
  - `/api/v1/catalog/artists`
  - `/api/v1/catalog/genres`
  - `/api/v1/catalog/lyrics`
  - `/api/v1/catalog/podcasts`
  - `/api/v1/catalog/episodes`
- Controller method return tiplerini generic olmayan şekilde düzenle.
  - `ResponseEntity` yerine `ResponseEntity<SongResponse>`
  - `ResponseEntity<List<SongResponse>>`
- Controller içindeki açıklama yorumlarını azalt; davranışı method isimleri ve DTO isimleriyle anlaşılır hale getir.
- Search endpointlerini sadeleştir.
  - İlk MVP için `GET /api/v1/catalog/songs/search?q=...`
  - Gelişmiş filtreler sonra eklenebilir.
- Bruno collection içine catalog endpointlerini ekle.
  - Artist create/list/detail
  - Album create/list/detail
  - Song upload/list/detail/search
  - Genre create/list

### DTO / Mapping

- Entity'leri response olarak direkt dönme yaklaşımını tamamen bırak.
- Her domain için create/update/response DTO setini tamamla.
- `CreateSongRequest` içinde multipart ile gelen alanları netleştir:
  - title
  - duration
  - albumId
  - genreIds
  - collaboratingArtistIds
  - audioFile
- `SongResponse` şu an album id/title dönüyor; artist, genre ve audioUrl bilgilerini kullanım senaryosuna göre genişlet.
- Mapping logic controller içinde değil service veya mapper sınıfında olmalı.
- MapStruct ekleyip eklemeyeceğini değerlendir; küçük MVP için manuel mapper yeterli.

### Repository / Query

- `SongRepository` içinde `JpaSpecificationExecutor` kullanımını doğrula.
- `SongSearchSpecificationBuilder` içinde join yapılan alanları kontrol et.
  - album
  - album.artist
  - genres
  - playlist exclusion
- `excludePlaylistId` query'sini catalog-service'ten çıkarmayı değerlendir.
- Search sorgularında N+1 problemine bak.
  - `Song -> Album`
  - `Album -> Artist`
  - `Song -> Genres`
- Listeleme endpointlerinde `findAll()` kullanımı yerine pagination'a geç.
- Sık kullanılan aramalar için index ihtiyacı değerlendir.
  - song.title
  - album.title
  - artist.name
  - genre.name

### Exception Handling

- Catalog service özel exception sınıfları ekle:
  - `SongNotFoundException`
  - `AlbumNotFoundException`
  - `ArtistNotFoundException`
  - `GenreNotFoundException`
  - `FileUploadException`
- `RuntimeException` kullanımını azalt.
- Global exception handler ile standart response dön:
  - timestamp
  - status
  - error
  - message
  - path
- Not found için 404, duplicate için 409, validation için 400, yetkisiz erişim için 401/403 dön.

### Event / Kafka

- Catalog eventlerini gerçekten kullanacak mısın karar ver.
- Şu eventler ikinci faza alınabilir:
  - `SongCreatedEvent`
  - `AlbumCreatedEvent`
  - `ArtistCreatedEvent`
- MVP'de Kafka ayağa kalkmadan catalog-service çalışabilmeli.
- Event publish işlemini create akışına ekleyeceksen DB commit sonrası publish etmeye dikkat et.
- Search index veya notification-service entegrasyonu gelene kadar Kafka eventleri TODO olarak kalabilir.

### First Implementation Order

- Catalog PostgreSQL container'ı compose'a ekle ve `spotify-catalog-postgresql` olarak ayağa kaldır.
- Catalog datasource'u yeni DB'ye bağla.
- Uygulamanın yeni boş DB ile açıldığını doğrula.
- Endpoint prefixlerini `/api/v1/catalog/...` olacak şekilde düzenle.
- İlk olarak `Genre` create/list/detail akışını test et.
- Sonra `Artist` create/list/detail akışını test et.
- Sonra `Album` create/list/detail akışını test et.
- Sonra `Song` upload/create/list/detail akışını test et.
- En son `SongSearchCriteria` ve specification bazlı search endpointini test et.
- Playlist, podcast ve episode akışlarını MVP sonrası faza bırak.

*** Container'lara gerekli olabilecek max veri için üst sınır koy.
*** Versiyonlama yedekleme için neler kullanılabilir ona bak.

