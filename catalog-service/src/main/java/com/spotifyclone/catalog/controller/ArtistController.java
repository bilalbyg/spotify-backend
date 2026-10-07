package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateArtistRequest;
import com.spotifyclone.catalog.dto.CreateBulkArtistRequest;
import com.spotifyclone.catalog.dto.ArtistResponse;
import com.spotifyclone.catalog.dto.UpdateArtistRequest;
import com.spotifyclone.catalog.service.ArtistService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // Sanatçı oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponse> createArtist(@ModelAttribute CreateArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.createArtist(request));
    }

    @AdminOnly
    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ArtistResponse>> createManyArtists(
            @RequestPart("artists") List<CreateBulkArtistRequest> artists,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        List<MultipartFile> safeImages = images != null ? images : List.of();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artistService.createManyArtists(artists, safeImages));
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable UUID id) {
        return ResponseEntity.ok(artistService.getArtistResponseById(id));
    }

    @AdminOnly
    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(@PathVariable UUID id, @RequestBody UpdateArtistRequest request) {
        return ResponseEntity.ok(artistService.updateArtist(id, request));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
