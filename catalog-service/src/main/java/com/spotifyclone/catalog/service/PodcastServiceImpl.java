package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreatePodcastRequest;
import com.spotifyclone.catalog.dto.PodcastResponse;
import com.spotifyclone.catalog.model.Podcast;
import com.spotifyclone.catalog.repository.PodcastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PodcastServiceImpl implements PodcastService {

    private final PodcastRepository podcastRepository;

    @Override
    public List<PodcastResponse> getAllPodcasts() {
        return podcastRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PodcastResponse createPodcast(CreatePodcastRequest request) {
        Podcast podcast = Podcast.builder()
                .title(request.title())
                .description(request.description())
                .publisher(request.publisher())
                .coverImageUrl(request.coverImageUrl())
                .build();
        podcast = podcastRepository.save(podcast);
        return mapToResponse(podcast);
    }

    @Override
    public PodcastResponse getPodcastById(UUID id) {
        Podcast podcast = podcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Podcast not found"));
        return mapToResponse(podcast);
    }

    @Override
    @Transactional
    public PodcastResponse updatePodcast(UUID id, CreatePodcastRequest request) {
        Podcast podcast = podcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Podcast not found"));
        podcast.setTitle(request.title());
        podcast.setDescription(request.description());
        podcast.setPublisher(request.publisher());
        podcast.setCoverImageUrl(request.coverImageUrl());
        podcast = podcastRepository.save(podcast);
        return mapToResponse(podcast);
    }

    @Override
    @Transactional
    public void deletePodcast(UUID id) {
        if (!podcastRepository.existsById(id)) {
            throw new RuntimeException("Podcast not found");
        }
        podcastRepository.deleteById(id);
    }

    private PodcastResponse mapToResponse(Podcast podcast) {
        return new PodcastResponse(
                podcast.getId(),
                podcast.getTitle(),
                podcast.getDescription(),
                podcast.getPublisher(),
                podcast.getCoverImageUrl()
        );
    }
}
