package com.spotifyclone.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SavedPlaylistResponse(
        UUID id,
        UUID userId,
        UUID playlistId,
        LocalDateTime savedAt
) {}
