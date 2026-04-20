package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record GenreResponse(
        UUID id,
        String name,
        String iconUrl
) {}
