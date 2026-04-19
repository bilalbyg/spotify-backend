package com.spotifyclone.catalog.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EpisodeResponse(
        UUID id,
        String title,
        String description,
        Integer duration,
        String audioUrl,
        LocalDate releaseDate,
        UUID podcastId
) {}
