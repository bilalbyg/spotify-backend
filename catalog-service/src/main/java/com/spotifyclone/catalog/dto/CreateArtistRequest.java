package com.spotifyclone.catalog.dto;

public record CreateArtistRequest(
        String name,
        String bio,
        String imageUrl
) {}