package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record PodcastResponse(
        UUID id,
        String title,
        String description,
        String publisher,
        String coverImageUrl
) {}
