package com.spotifyclone.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SavedAlbumResponse(
        UUID id,
        UUID userId,
        UUID albumId,
        LocalDateTime savedAt
) {}
