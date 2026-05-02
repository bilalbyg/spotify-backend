package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateEpisodeRequest;
import com.spotifyclone.catalog.dto.EpisodeResponse;
import com.spotifyclone.catalog.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping
    public ResponseEntity<List<EpisodeResponse>> getAllEpisodes() {
        return ResponseEntity.ok(episodeService.getAllEpisodes());
    }

    // Bölüm oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping
    public ResponseEntity<EpisodeResponse> createEpisode(@RequestBody CreateEpisodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(episodeService.createEpisode(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpisodeResponse> getEpisodeById(@PathVariable UUID id) {
        return ResponseEntity.ok(episodeService.getEpisodeById(id));
    }

    // Bölüm güncelleme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PutMapping("/{id}")
    public ResponseEntity<EpisodeResponse> updateEpisode(@PathVariable UUID id, @RequestBody CreateEpisodeRequest request) {
        return ResponseEntity.ok(episodeService.updateEpisode(id, request));
    }

    // Bölüm silme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable UUID id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
}
