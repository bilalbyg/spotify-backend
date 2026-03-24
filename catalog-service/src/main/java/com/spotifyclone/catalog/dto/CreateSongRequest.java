package com.spotifyclone.catalog.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record CreateSongRequest(
        String title,
        Integer duration,         // Şarkı süresi (Saniye)
        UUID albumId,             // İlişkisel bağımız!
        MultipartFile audioFile // MP3 dosyamız
) {}