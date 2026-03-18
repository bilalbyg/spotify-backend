package com.spotifyclone.catalog.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateArtistRequest(
        String name,
        String bio,
        MultipartFile image) {
}