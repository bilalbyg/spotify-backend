package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.LikedSongRequest;
import com.spotifyclone.catalog.dto.LikedSongResponse;
import com.spotifyclone.catalog.service.LikedSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/songs")
@RequiredArgsConstructor
public class LikedSongController {

    private final LikedSongService likedSongService;

    @PostMapping
    public ResponseEntity<LikedSongResponse> likeSong(@RequestBody LikedSongRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likedSongService.likeSong(request));
    }

    @DeleteMapping("/{userId}/{songId}")
    public ResponseEntity<Void> unlikeSong(@PathVariable UUID userId, @PathVariable UUID songId) {
        likedSongService.unlikeSong(userId, songId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<LikedSongResponse>> getLikedSongs(@PathVariable UUID userId) {
        return ResponseEntity.ok(likedSongService.getLikedSongs(userId));
    }
}
