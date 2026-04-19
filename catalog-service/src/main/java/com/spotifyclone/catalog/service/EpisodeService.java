package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateEpisodeRequest;
import com.spotifyclone.catalog.dto.EpisodeResponse;

import java.util.List;
import java.util.UUID;

public interface EpisodeService {
    List<EpisodeResponse> getAllEpisodes();
    EpisodeResponse createEpisode(CreateEpisodeRequest request);
    EpisodeResponse getEpisodeById(UUID id);
    EpisodeResponse updateEpisode(UUID id, CreateEpisodeRequest request);
    void deleteEpisode(UUID id);
}
