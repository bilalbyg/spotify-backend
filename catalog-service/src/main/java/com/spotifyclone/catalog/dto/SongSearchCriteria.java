package com.spotifyclone.catalog.dto;

import java.util.List;
import java.util.UUID;

// Şarkı arama ekranından gelebilecek tüm filtrelerin tek DTO altında taşınan hali.
public record SongSearchCriteria(
        // Şarkı başlığında geçen ifade (contains).
        String title,
        // Albüm sanatçısının adında geçen ifade (contains).
        String artistName,
        // Dahil edilmesi istenen genre id listesi.
        List<UUID> genreIds,
        // Süre alt sınırı (saniye).
        Integer minDuration,
        // Süre üst sınırı (saniye).
        Integer maxDuration,
        // Albüm çıkış yılı alt sınırı.
        Integer releaseYearFrom,
        // Albüm çıkış yılı üst sınırı.
        Integer releaseYearTo,
        // Bu playlist içinde olan şarkıları arama sonucundan çıkar.
        UUID excludePlaylistId
) {
}
