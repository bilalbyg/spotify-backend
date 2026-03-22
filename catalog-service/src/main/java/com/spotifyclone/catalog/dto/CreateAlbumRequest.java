package com.spotifyclone.catalog.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record CreateAlbumRequest(
        String title,
        Integer releaseYear,
        UUID artistId,
        MultipartFile image
) {}