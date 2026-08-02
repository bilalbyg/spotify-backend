package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.AlbumResponse;
import com.spotifyclone.catalog.dto.CreateAlbumRequest;
import com.spotifyclone.catalog.dto.DeleteAlbumResponse;
import com.spotifyclone.catalog.service.AlbumService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // Albüm oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponse> createAlbum(@ModelAttribute CreateAlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(request));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable UUID artistId) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistId));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumResponse>> searchAlbums(@RequestParam(required = false) String title,
                                                            @RequestParam(required = false) String artistName,
                                                            @RequestParam(required = false) Integer releaseYearFrom,
                                                            @RequestParam(required = false) Integer releaseYearTo) {
        return ResponseEntity.ok(albumService.searchAlbums(
                title,
                artistName,
                releaseYearFrom,
                releaseYearTo
        ));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(albumService.searchAlbumsByTitle(title));
    }

    @GetMapping("/search/artist")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByArtistName(@RequestParam String artistName) {
        return ResponseEntity.ok(albumService.searchAlbumsByArtistName(artistName));
    }

    @GetMapping("/search/release-year-from")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByReleaseYearFrom(@RequestParam Integer releaseYearFrom) {
        return ResponseEntity.ok(albumService.searchAlbumsByReleaseYearFrom(releaseYearFrom));
    }

    @GetMapping("/search/release-year-to")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByReleaseYearTo(@RequestParam Integer releaseYearTo) {
        return ResponseEntity.ok(albumService.searchAlbumsByReleaseYearTo(releaseYearTo));
    }

    @GetMapping("/search/release-year-range")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByReleaseYearRange(@RequestParam Integer releaseYearFrom,
                                                                              @RequestParam Integer releaseYearTo) {
        return ResponseEntity.ok(albumService.searchAlbumsByReleaseYearRange(
                releaseYearFrom,
                releaseYearTo
        ));
    }

    @GetMapping("/search/title-artist")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByTitleAndArtistName(@RequestParam String title,
                                                                                @RequestParam String artistName) {
        return ResponseEntity.ok(albumService.searchAlbumsByTitleAndArtistName(title, artistName));
    }

    @GetMapping("/search/title-release-year")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByTitleAndReleaseYearRange(@RequestParam String title,
                                                                                      @RequestParam Integer releaseYearFrom,
                                                                                      @RequestParam Integer releaseYearTo) {
        return ResponseEntity.ok(albumService.searchAlbumsByTitleAndReleaseYearRange(
                title,
                releaseYearFrom,
                releaseYearTo
        ));
    }

    @GetMapping("/search/artist-release-year")
    public ResponseEntity<List<AlbumResponse>> searchAlbumsByArtistNameAndReleaseYearRange(@RequestParam String artistName,
                                                                                           @RequestParam Integer releaseYearFrom,
                                                                                           @RequestParam Integer releaseYearTo) {
        return ResponseEntity.ok(albumService.searchAlbumsByArtistNameAndReleaseYearRange(
                artistName,
                releaseYearFrom,
                releaseYearTo
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.getAlbumResponseById(id));
    }

    @AdminOnly
    @DeleteMapping()
    public ResponseEntity<DeleteAlbumResponse> deleteAll() {
        return ResponseEntity.ok(albumService.deleteAll());
    }
}
