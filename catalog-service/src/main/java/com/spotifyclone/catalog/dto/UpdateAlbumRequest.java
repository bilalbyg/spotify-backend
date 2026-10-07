package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record UpdateAlbumRequest(
        String title,
        Integer releaseYear,
        UUID artistId
) {}
