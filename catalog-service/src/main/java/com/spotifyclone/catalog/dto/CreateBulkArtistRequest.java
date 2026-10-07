package com.spotifyclone.catalog.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateBulkArtistRequest(
        String name,
        String bio,
        Integer imageIndex
) {}
