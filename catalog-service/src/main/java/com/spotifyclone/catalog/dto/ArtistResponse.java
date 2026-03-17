package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record ArtistResponse(
        UUID id,
        String name,
        String bio,
        String imageUrl,
        int popularity
) {}