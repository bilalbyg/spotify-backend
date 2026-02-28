// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/dto/SongResponse.java
package com.spotifyclone.catalog.dto;

import java.util.UUID;

// Record'lar arka planda getter, constructor ve equals/hashCode metodlarını otomatik oluşturur. Immutable (Değiştirilemez) yapılardır.
public record SongResponse(
        UUID id,
        String title,
        String artist,
        String albumImageUrl,
        String audioUrl
) {
}