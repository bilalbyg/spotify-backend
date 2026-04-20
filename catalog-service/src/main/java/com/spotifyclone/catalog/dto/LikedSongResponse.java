package com.spotifyclone.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LikedSongResponse(
        UUID id,
        UUID userId,
        UUID songId,
        LocalDateTime likedAt
) {}
