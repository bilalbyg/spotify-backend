package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.SavedAlbumRequest;
import com.spotifyclone.catalog.dto.SavedAlbumResponse;
import com.spotifyclone.catalog.service.SavedAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library/albums")
@RequiredArgsConstructor
public class SavedAlbumController {

    private final SavedAlbumService savedAlbumService;

    @PostMapping
    public ResponseEntity<SavedAlbumResponse> saveAlbum(@RequestBody SavedAlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAlbumService.saveAlbum(request));
    }

    @DeleteMapping("/{userId}/{albumId}")
    public ResponseEntity<Void> unsaveAlbum(@PathVariable UUID userId, @PathVariable UUID albumId) {
        savedAlbumService.unsaveAlbum(userId, albumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedAlbumResponse>> getSavedAlbums(@PathVariable UUID userId) {
        return ResponseEntity.ok(savedAlbumService.getSavedAlbums(userId));
    }
}
