package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreatePodcastRequest;
import com.spotifyclone.catalog.dto.PodcastResponse;
import com.spotifyclone.catalog.service.PodcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PodcastResponse> createPodcast(@RequestBody CreatePodcastRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(podcastService.createPodcast(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PodcastResponse> getPodcastById(@PathVariable UUID id) {
        return ResponseEntity.ok(podcastService.getPodcastById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PodcastResponse> updatePodcast(@PathVariable UUID id, @RequestBody CreatePodcastRequest request) {
        return ResponseEntity.ok(podcastService.updatePodcast(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePodcast(@PathVariable UUID id) {
        podcastService.deletePodcast(id);
        return ResponseEntity.noContent().build();
    }
}
