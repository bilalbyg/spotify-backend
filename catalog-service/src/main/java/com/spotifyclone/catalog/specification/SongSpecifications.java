package com.spotifyclone.catalog.specification;

import com.spotifyclone.catalog.model.Song;
import org.springframework.data.jpa.domain.Specification;

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
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        });
    }
}
