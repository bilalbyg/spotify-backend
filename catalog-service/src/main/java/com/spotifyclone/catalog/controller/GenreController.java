package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateGenreRequest;
import com.spotifyclone.catalog.dto.GenreResponse;
import com.spotifyclone.catalog.service.GenreService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // Tür oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(@RequestBody CreateGenreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenre(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable UUID id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    // Tür güncelleme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> updateGenre(@PathVariable UUID id, @RequestBody CreateGenreRequest request) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }

    // Tür silme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
