package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateEpisodeRequest;
import com.spotifyclone.catalog.dto.EpisodeResponse;
import com.spotifyclone.catalog.model.Episode;
import com.spotifyclone.catalog.model.Podcast;
import com.spotifyclone.catalog.repository.EpisodeRepository;
import com.spotifyclone.catalog.repository.PodcastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final PodcastRepository podcastRepository;

    @Override
    public List<EpisodeResponse> getAllEpisodes() {
        return episodeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EpisodeResponse createEpisode(CreateEpisodeRequest request) {
        Podcast podcast = podcastRepository.findById(request.podcastId())
                .orElseThrow(() -> new RuntimeException("Podcast not found"));

        Episode episode = Episode.builder()
                .title(request.title())
                .description(request.description())
                .duration(request.duration())
                .audioUrl(request.audioUrl())
                .releaseDate(request.releaseDate())
                .podcast(podcast)
                .build();
        episode = episodeRepository.save(episode);
        return mapToResponse(episode);
    }

    @Override
    public EpisodeResponse getEpisodeById(UUID id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        return mapToResponse(episode);
    }

    @Override
    @Transactional
    public EpisodeResponse updateEpisode(UUID id, CreateEpisodeRequest request) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        Podcast podcast = podcastRepository.findById(request.podcastId())
                .orElseThrow(() -> new RuntimeException("Podcast not found"));

        episode.setTitle(request.title());
        episode.setDescription(request.description());
        episode.setDuration(request.duration());
        episode.setAudioUrl(request.audioUrl());
        episode.setReleaseDate(request.releaseDate());
        episode.setPodcast(podcast);

        episode = episodeRepository.save(episode);
        return mapToResponse(episode);
    }

    @Override
    @Transactional
    public void deleteEpisode(UUID id) {
        if (!episodeRepository.existsById(id)) {
            throw new RuntimeException("Episode not found");
        }
        episodeRepository.deleteById(id);
    }

    private EpisodeResponse mapToResponse(Episode episode) {
        return new EpisodeResponse(
                episode.getId(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getDuration(),
                episode.getAudioUrl(),
                episode.getReleaseDate(),
                episode.getPodcast().getId()
        );
    }
}
