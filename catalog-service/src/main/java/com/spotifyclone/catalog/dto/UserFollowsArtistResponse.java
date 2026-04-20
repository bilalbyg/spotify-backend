package com.spotifyclone.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserFollowsArtistResponse(
        UUID id,
        UUID userId,
        UUID artistId,
        LocalDateTime followedAt
) {}
