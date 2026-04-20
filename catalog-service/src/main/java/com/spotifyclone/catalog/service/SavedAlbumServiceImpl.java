package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.SavedAlbumRequest;
import com.spotifyclone.catalog.dto.SavedAlbumResponse;
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.SavedAlbum;
import com.spotifyclone.catalog.repository.AlbumRepository;
import com.spotifyclone.catalog.repository.SavedAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedAlbumServiceImpl implements SavedAlbumService {

    private final SavedAlbumRepository savedAlbumRepository;
    private final AlbumRepository albumRepository;

    @Override
    @Transactional
    public SavedAlbumResponse saveAlbum(SavedAlbumRequest request) {
        if (savedAlbumRepository.existsByUserIdAndAlbumId(request.userId(), request.albumId())) {
            throw new RuntimeException("Album already saved");
        }

        Album album = albumRepository.findById(request.albumId())
                .orElseThrow(() -> new RuntimeException("Album not found"));

        SavedAlbum savedAlbum = SavedAlbum.builder()
                .userId(request.userId())
                .album(album)
                .savedAt(LocalDateTime.now())
                .build();

        savedAlbum = savedAlbumRepository.save(savedAlbum);
        return mapToResponse(savedAlbum);
    }

    @Override
    @Transactional
    public void unsaveAlbum(UUID userId, UUID albumId) {
        SavedAlbum savedAlbum = savedAlbumRepository.findByUserIdAndAlbumId(userId, albumId)
                .orElseThrow(() -> new RuntimeException("Saved album not found"));
        savedAlbumRepository.delete(savedAlbum);
    }

    @Override
    public List<SavedAlbumResponse> getSavedAlbums(UUID userId) {
        return savedAlbumRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SavedAlbumResponse mapToResponse(SavedAlbum savedAlbum) {
        return new SavedAlbumResponse(
                savedAlbum.getId(),
                savedAlbum.getUserId(),
                savedAlbum.getAlbum().getId(),
                savedAlbum.getSavedAt()
        );
    }
}
