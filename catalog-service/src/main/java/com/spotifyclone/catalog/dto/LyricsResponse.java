package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record LyricsResponse(
        UUID id,
        String text,
        UUID songId
) {}
