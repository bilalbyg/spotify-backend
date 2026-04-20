package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record LikedSongRequest(
        UUID userId,
        UUID songId
) {}
