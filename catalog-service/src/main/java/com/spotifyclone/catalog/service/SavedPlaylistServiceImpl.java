package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.SavedPlaylistRequest;
import com.spotifyclone.catalog.dto.SavedPlaylistResponse;
import com.spotifyclone.catalog.model.Playlist;
import com.spotifyclone.catalog.model.SavedPlaylist;
import com.spotifyclone.catalog.repository.PlaylistRepository;
import com.spotifyclone.catalog.repository.SavedPlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedPlaylistServiceImpl implements SavedPlaylistService {

    private final SavedPlaylistRepository savedPlaylistRepository;
    private final PlaylistRepository playlistRepository;

    @Override
    @Transactional
    public SavedPlaylistResponse savePlaylist(SavedPlaylistRequest request) {
        if (savedPlaylistRepository.existsByUserIdAndPlaylistId(request.userId(), request.playlistId())) {
            throw new RuntimeException("Playlist already saved");
        }

        Playlist playlist = playlistRepository.findById(request.playlistId())
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        SavedPlaylist savedPlaylist = SavedPlaylist.builder()
                .userId(request.userId())
                .playlist(playlist)
                .savedAt(LocalDateTime.now())
                .build();

        savedPlaylist = savedPlaylistRepository.save(savedPlaylist);
        return mapToResponse(savedPlaylist);
    }

    @Override
    @Transactional
    public void unsavePlaylist(UUID userId, UUID playlistId) {
        SavedPlaylist savedPlaylist = savedPlaylistRepository.findByUserIdAndPlaylistId(userId, playlistId)
                .orElseThrow(() -> new RuntimeException("Saved playlist not found"));
        savedPlaylistRepository.delete(savedPlaylist);
    }

    @Override
    public List<SavedPlaylistResponse> getSavedPlaylists(UUID userId) {
        return savedPlaylistRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SavedPlaylistResponse mapToResponse(SavedPlaylist savedPlaylist) {
        return new SavedPlaylistResponse(
                savedPlaylist.getId(),
                savedPlaylist.getUserId(),
                savedPlaylist.getPlaylist().getId(),
                savedPlaylist.getSavedAt()
        );
    }
}
