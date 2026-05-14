package com.spotifyclone.catalog.specification;

// Song ile ilişkili entity alanlarına join atmak için model importları.
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.model.Genre;
import com.spotifyclone.catalog.model.Playlist;
import com.spotifyclone.catalog.model.Song;
// Criteria API tarafında join/subquery kurmak için gerekli tipler.
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Subquery;
// Spring Data specification sözleşmesi.
import org.springframework.data.jpa.domain.Specification;

// Girdi koleksiyonları ve tip güvenliği için kullanılan Java yardımcı tipleri.
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

// Song entity'si için tekrar kullanılabilir dinamik sorgu kurallarını burada tutuyoruz.
public final class SongSpecifications {

    // Utility sınıfı gibi kullanılacağı için dışarıdan instance alınmasını istemiyoruz.
    private SongSpecifications() {
    }

    // Title alanında "contains" mantığı ile (buyuk/kucuk harf duyarsiz) arama filtresi uretir.
    public static Specification<Song> titleContains(String title) {
        // Specification, JPA Criteria API'nin (root, query, criteriaBuilder) uclusunu kullanir.
        return ((root, query, criteriaBuilder) -> {
            // Kullanici title gondermediyse filtre uygulamiyoruz (always-true predicate).
            if (title == null || title.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            // 1) DB tarafindaki title'i lower() ile kucuk harfe cevir
            // 2) Gelen title'i Java tarafinda kucuk harfe cevir
            // 3) LIKE %...% ile title icinde gecip gecmedigini kontrol et
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase(Locale.ROOT) + "%"
            );
        });
    }

    // Duration alaninda ">= minDuration" mantigi ile arama filtresi uretir.
    public static Specification<Song> durationGreaterThanOrEqual(Integer minDuration) {
        return ((root, query, criteriaBuilder) -> {
            // Kullanici sure gondermediyse bu filtreyi devre disi birakiyoruz.
            if (minDuration == null) {
                return criteriaBuilder.conjunction();
            }

            // duration >= minDuration kosulu
            return criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration);
        });
    }

    // Duration alaninda "<= maxDuration" mantigi ile arama filtresi uretir.
    public static Specification<Song> durationLessThanOrEqual(Integer maxDuration) {
        return ((root, query, criteriaBuilder) -> {
            // Max süre verilmediyse bu filtreden bağımsız devam edilir.
            if (maxDuration == null) {
                return criteriaBuilder.conjunction();
            }
            // duration <= maxDuration koşulu.
            return criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration);
        });
    }

    // Album artist name alaninda contains mantigi ile (buyuk/kucuk harf duyarsiz) arama filtresi uretir.
    public static Specification<Song> artistNameContains(String artistName) {
        return ((root, query, criteriaBuilder) -> {
            // Artist adı yoksa filtreyi pas geç.
            if (artistName == null || artistName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            // Join kaynaklı çoğalmayı engellemek için distinct sonuç seti.
            if (query != null) {
                query.distinct(true);
            }

            // Song -> Album join'i.
            Join<Song, Album> albumJoin = root.join("album", JoinType.INNER);
            // Album -> Artist join'i.
            Join<Album, Artist> artistJoin = albumJoin.join("artist", JoinType.INNER);
            // Artist name üzerinde case-insensitive contains filtresi.
            return criteriaBuilder.like(
                    criteriaBuilder.lower(artistJoin.get("name")),
                    "%" + artistName.toLowerCase(Locale.ROOT) + "%"
            );
        });
    }

    // Song -> album.releaseYear icin ">= releaseYearFrom" filtresi.
    public static Specification<Song> releaseYearGreaterThanOrEqual(Integer releaseYearFrom) {
        return ((root, query, criteriaBuilder) -> {
            // Alt yıl sınırı yoksa koşulu devre dışı bırak.
            if (releaseYearFrom == null) {
                return criteriaBuilder.conjunction();
            }
            // Şarkının bağlı olduğu albüme erişmek için join.
            Join<Song, Album> albumJoin = root.join("album", JoinType.INNER);
            // album.releaseYear >= releaseYearFrom koşulu.
            return criteriaBuilder.greaterThanOrEqualTo(albumJoin.get("releaseYear"), releaseYearFrom);
        });
    }

    // Song -> album.releaseYear icin "<= releaseYearTo" filtresi.
    public static Specification<Song> releaseYearLessThanOrEqual(Integer releaseYearTo) {
        return ((root, query, criteriaBuilder) -> {
            // Üst yıl sınırı yoksa koşulu devre dışı bırak.
            if (releaseYearTo == null) {
                return criteriaBuilder.conjunction();
            }
            // Şarkının bağlı olduğu albüme erişmek için join.
            Join<Song, Album> albumJoin = root.join("album", JoinType.INNER);
            // album.releaseYear <= releaseYearTo koşulu.
            return criteriaBuilder.lessThanOrEqualTo(albumJoin.get("releaseYear"), releaseYearTo);
        });
    }

    // Song genre id listesi icin IN filtresi.
    public static Specification<Song> genreIdIn(Collection<UUID> genreIds) {
        return ((root, query, criteriaBuilder) -> {
            // Genre listesi boşsa filtreyi uygulamadan devam et.
            if (genreIds == null || genreIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Null id'leri temizleyerek geçerli bir filtre listesi üret.
            List<UUID> validGenreIds = genreIds.stream()
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            // Temizlenmiş liste de boşsa filtreyi atla.
            if (validGenreIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Çoktan-çoğa join'de tekrarları engellemek için distinct.
            if (query != null) {
                query.distinct(true);
            }

            // Song -> Genre many-to-many join'i.
            Join<Song, Genre> genreJoin = root.join("genres", JoinType.LEFT);
            // Genre id alanı üzerinden IN filtresi.
            return genreJoin.get("id").in(validGenreIds);
        });
    }

    // Belirli bir playlistteki sarkilari sonuc listesinden cikarir.
    public static Specification<Song> excludePlaylistSongs(UUID playlistId) {
        return ((root, query, criteriaBuilder) -> {
            // Playlist id verilmediyse herhangi bir dışlama yapma.
            if (playlistId == null) {
                return criteriaBuilder.conjunction();
            }

            // Bazı iç çağrılarda query null gelebilir, bu durumda güvenli şekilde filtreyi pas geç.
            if (query == null) {
                return criteriaBuilder.conjunction();
            }

            // Alt sorgu/join nedeniyle oluşabilecek duplicate kayıtları engelle.
            query.distinct(true);

            // Playlist içindeki song id'lerini bulacak alt sorgu.
            Subquery<UUID> subquery = query.subquery(UUID.class);
            // Alt sorgu kökü: Playlist.
            var playlistRoot = subquery.from(Playlist.class);
            // Playlist -> songs join'i.
            Join<Playlist, Song> playlistSongsJoin = playlistRoot.join("songs", JoinType.INNER);

            // Seçilecek değer: playlist'teki song id'leri.
            subquery.select(playlistSongsJoin.get("id"))
                    // Sadece ilgili playlist'e ait song id'leri.
                    .where(criteriaBuilder.equal(playlistRoot.get("id"), playlistId));

            // Ana sorguda, alt sorgudaki id'leri dışlayarak filtre uygula.
            return criteriaBuilder.not(root.get("id").in(subquery));
        });
    }
}
