package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateArtistRequest;
import com.spotifyclone.catalog.dto.ArtistResponse;
import com.spotifyclone.catalog.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponse> createArtist(@ModelAttribute CreateArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(request));
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }
}