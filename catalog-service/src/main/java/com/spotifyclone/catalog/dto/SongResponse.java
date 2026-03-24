package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record SongResponse(
        UUID id,
        String title,
        Integer duration,
        String audioUrl,
        UUID albumId,
        String albumTitle
) {}