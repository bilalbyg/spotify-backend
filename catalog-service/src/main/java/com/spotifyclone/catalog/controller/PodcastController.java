package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreatePodcastRequest;
import com.spotifyclone.catalog.dto.PodcastResponse;
import com.spotifyclone.catalog.service.PodcastService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final PodcastService podcastService;

    @GetMapping
    public ResponseEntity<List<PodcastResponse>> getAllPodcasts() {
        return ResponseEntity.ok(podcastService.getAllPodcasts());
    }

    // Podcast oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping
    public ResponseEntity<PodcastResponse> createPodcast(@RequestBody CreatePodcastRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(podcastService.createPodcast(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PodcastResponse> getPodcastById(@PathVariable UUID id) {
        return ResponseEntity.ok(podcastService.getPodcastById(id));
    }

    // Podcast güncelleme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PutMapping("/{id}")
    public ResponseEntity<PodcastResponse> updatePodcast(@PathVariable UUID id, @RequestBody CreatePodcastRequest request) {
        return ResponseEntity.ok(podcastService.updatePodcast(id, request));
    }

    // Podcast silme işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePodcast(@PathVariable UUID id) {
        podcastService.deletePodcast(id);
        return ResponseEntity.noContent().build();
    }
}
