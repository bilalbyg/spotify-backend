package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateLyricsRequest;
import com.spotifyclone.catalog.dto.LyricsResponse;
import com.spotifyclone.catalog.service.LyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lyrics")
@RequiredArgsConstructor
public class LyricsController {

    private final LyricsService lyricsService;

    @PostMapping
    public ResponseEntity<LyricsResponse> createOrUpdateLyrics(@RequestBody CreateLyricsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lyricsService.createOrUpdateLyrics(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LyricsResponse> getLyricsById(@PathVariable UUID id) {
        return ResponseEntity.ok(lyricsService.getLyricsById(id));
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<LyricsResponse> getLyricsBySongId(@PathVariable UUID songId) {
        return ResponseEntity.ok(lyricsService.getLyricsBySongId(songId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLyrics(@PathVariable UUID id) {
        lyricsService.deleteLyrics(id);
        return ResponseEntity.noContent().build();
    }
}
