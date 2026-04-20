package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.SavedPlaylistRequest;
import com.spotifyclone.catalog.dto.SavedPlaylistResponse;
import com.spotifyclone.catalog.service.SavedPlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/playlists")
@RequiredArgsConstructor
public class SavedPlaylistController {

    private final SavedPlaylistService savedPlaylistService;

    @PostMapping
    public ResponseEntity<SavedPlaylistResponse> savePlaylist(@RequestBody SavedPlaylistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlaylistService.savePlaylist(request));
    }

    @DeleteMapping("/{userId}/{playlistId}")
    public ResponseEntity<Void> unsavePlaylist(@PathVariable UUID userId, @PathVariable UUID playlistId) {
        savedPlaylistService.unsavePlaylist(userId, playlistId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedPlaylistResponse>> getSavedPlaylists(@PathVariable UUID userId) {
        return ResponseEntity.ok(savedPlaylistService.getSavedPlaylists(userId));
    }
}
