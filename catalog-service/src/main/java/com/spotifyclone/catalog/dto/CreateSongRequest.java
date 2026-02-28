// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/dto/CreateSongRequest.java
package com.spotifyclone.catalog.dto;

public record CreateSongRequest(
        String title,
        String artist,
        String albumImageUrl,
        String audioUrl
) {
}