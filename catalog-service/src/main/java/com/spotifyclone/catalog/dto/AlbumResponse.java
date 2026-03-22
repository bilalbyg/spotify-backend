package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record AlbumResponse(
        UUID id,
        String title,
        Integer releaseYear,
        String coverImageUrl,
        UUID artistId,
        String artistName
) {}