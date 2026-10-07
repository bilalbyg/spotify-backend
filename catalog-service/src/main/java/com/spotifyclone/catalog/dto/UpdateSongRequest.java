package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record UpdateSongRequest(
        String title,
        Integer duration,
        UUID albumId
) {}
