package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record CreateLyricsRequest(
        String text,
        UUID songId
) {}
