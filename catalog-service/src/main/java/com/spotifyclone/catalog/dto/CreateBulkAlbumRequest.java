package com.spotifyclone.catalog.dto;

public record CreateBulkAlbumRequest(
        String title,
        Integer releaseYear,
        Integer imageIndex
) {}
