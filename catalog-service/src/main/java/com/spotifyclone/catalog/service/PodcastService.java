package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreatePodcastRequest;
import com.spotifyclone.catalog.dto.PodcastResponse;

import java.util.List;
import java.util.UUID;

public interface PodcastService {
    List<PodcastResponse> getAllPodcasts();
    PodcastResponse createPodcast(CreatePodcastRequest request);
    PodcastResponse getPodcastById(UUID id);
    PodcastResponse updatePodcast(UUID id, CreatePodcastRequest request);
    void deletePodcast(UUID id);
}
