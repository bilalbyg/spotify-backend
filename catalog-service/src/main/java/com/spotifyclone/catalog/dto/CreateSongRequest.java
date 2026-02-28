package com.spotifyclone.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSongRequest(
        @NotBlank(message = "{song.title.blank}")
        @Size(min = 2, message = "{song.title.size}")
        String title,

        @NotBlank(message = "{song.artist.blank}")
        String artist,

        String albumImageUrl,
        String audioUrl
) { }