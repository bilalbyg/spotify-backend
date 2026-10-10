package com.spotifyclone.catalog.dto;

public record CreateBulkSongRequest(
        String title,
        Integer duration,
        Integer audioIndex
) {}
