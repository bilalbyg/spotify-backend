package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record PlaylistResponse(
        UUID id,
        String name,
        String description,
        String coverImageUrl,
        boolean isPublic,
        UUID ownerId
) {}
