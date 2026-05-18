package com.spotifyclone.catalog.specification;

import com.spotifyclone.catalog.dto.SongSearchCriteria;
import com.spotifyclone.catalog.model.Song;
import org.springframework.data.jpa.domain.Specification;

// Song arama kriterlerini tek bir specification zincirine cevirir.
public final class SongSearchSpecificationBuilder {

    // Utility class: dışarıdan örneklenmesini engelliyoruz.
    private SongSearchSpecificationBuilder() {
    }

    // Kriter DTO'sunu repository'nin çalıştırabileceği tek bir Specification zincirine dönüştürür.
    public static Specification<Song> build(SongSearchCriteria criteria) {
        // Kriter yoksa filtre uygulamadan tüm kayıtları döndüren null-spec yaklaşımı.
        if (criteria == null) {
            return Specification.where((Specification<Song>) null);
        }

        // Aralıklar ters verildiyse (örn min>max), veritabanına hatalı sorgu göndermeden boş sonuç dön.
        if (isInvalidRange(criteria.minDuration(), criteria.maxDuration())
                || isInvalidRange(criteria.releaseYearFrom(), criteria.releaseYearTo())) {
            // disjunction() = always-false predicate.
            return ((root, query, criteriaBuilder) -> criteriaBuilder.disjunction());
        }

        // Her filtreyi AND ile birleştiriyoruz; null/boş olan filtreler specification içinde no-op olur.
        return Specification.where(SongSpecifications.titleContains(criteria.title()))
                .and(SongSpecifications.artistNameContains(criteria.artistName()))
                .and(SongSpecifications.genreIdIn(criteria.genreIds()))
                .and(SongSpecifications.durationGreaterThanOrEqual(criteria.minDuration()))
                .and(SongSpecifications.durationLessThanOrEqual(criteria.maxDuration()))
                .and(SongSpecifications.releaseYearGreaterThanOrEqual(criteria.releaseYearFrom()))
                .and(SongSpecifications.releaseYearLessThanOrEqual(criteria.releaseYearTo()))
                .and(SongSpecifications.excludePlaylistSongs(criteria.excludePlaylistId()));
    }

    // Genel amaçlı min/max doğrulaması.
    private static boolean isInvalidRange(Integer min, Integer max) {
        // İki sınır da varsa ve min > max ise aralık geçersizdir.
        return min != null && max != null && min > max;
    }
}
