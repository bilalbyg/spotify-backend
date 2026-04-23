package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> createSong(@ModelAttribute CreateSongRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.createSong(request));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<SongResponse>> getSongsByAlbum(@PathVariable UUID albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable UUID id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<SongResponse>> getSongsByArtist(@PathVariable UUID artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }
}