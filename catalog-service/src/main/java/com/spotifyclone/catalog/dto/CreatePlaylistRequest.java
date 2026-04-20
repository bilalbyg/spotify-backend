package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record CreatePlaylistRequest(
        String name,
        String description,
        String coverImageUrl,
        boolean isPublic,
        UUID ownerId
) {}
