package com.spotifyclone.catalog.dto;

public record DeleteAlbumResponse(
        long deletedAlbumCount,
        String message) {}
