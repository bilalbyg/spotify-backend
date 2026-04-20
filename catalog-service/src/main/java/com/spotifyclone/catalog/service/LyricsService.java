package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateLyricsRequest;
import com.spotifyclone.catalog.dto.LyricsResponse;

import java.util.UUID;

public interface LyricsService {
    LyricsResponse createOrUpdateLyrics(CreateLyricsRequest request);
    LyricsResponse getLyricsById(UUID id);
    LyricsResponse getLyricsBySongId(UUID songId);
    void deleteLyrics(UUID id);
}
