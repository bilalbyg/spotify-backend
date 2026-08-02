package com.spotifyclone.catalog.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record CreateSongRequest(
        String title,
        Integer duration,
        UUID albumId,
        MultipartFile audioFile
) {}