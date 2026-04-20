package com.spotifyclone.catalog.dto;

public record CreatePodcastRequest(
        String title,
        String description,
        String publisher,
        String coverImageUrl
) {}
