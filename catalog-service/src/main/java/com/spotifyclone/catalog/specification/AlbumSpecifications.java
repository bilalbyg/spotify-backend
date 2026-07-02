package com.spotifyclone.catalog.specification;

import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Artist;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class AlbumSpecifications {
    private AlbumSpecifications() {
    }

    public static Specification<Album> artistNameContains(String artistName) {
        return (root, query, criteriaBuilder) -> {
            if (artistName == null || artistName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            if (query != null) {
                query.distinct(true);
            }

            Join<Album, Artist> artistJoin = root.join("artist", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.lower(artistJoin.<String>get("name")),
                    "%" + artistName.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    public static Specification<Album> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.<String>get("title")),
                    "%" + title.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    public static Specification<Album> releaseYearGreaterThanOrEqual(Integer releaseYearFrom) {
        return (root, query, criteriaBuilder) -> {
            if (releaseYearFrom == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.<Integer>get("releaseYear"), releaseYearFrom);
        };
    }

    public static Specification<Album> releaseYearLessThanOrEqual(Integer releaseYearTo) {
        return (root, query, criteriaBuilder) -> {
            if (releaseYearTo == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.<Integer>get("releaseYear"), releaseYearTo);
        };
    }
}
