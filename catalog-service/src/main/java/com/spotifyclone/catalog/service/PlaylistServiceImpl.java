package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreatePlaylistRequest;
import com.spotifyclone.catalog.dto.PlaylistResponse;
import com.spotifyclone.catalog.model.Playlist;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.PlaylistRepository;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Override
    public List<PlaylistResponse> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request) {
        Playlist playlist = Playlist.builder()
                .name(request.name())
                .description(request.description())
                .coverImageUrl(request.coverImageUrl())
                .isPublic(request.isPublic())
                .ownerId(request.ownerId())
                .songs(new HashSet<>())
                .build();
        playlist = playlistRepository.save(playlist);
        return mapToResponse(playlist);
    }

    @Override
    public PlaylistResponse getPlaylistById(UUID id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return mapToResponse(playlist);
    }

    @Override
    @Transactional
    public PlaylistResponse updatePlaylist(UUID id, CreatePlaylistRequest request) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        playlist.setName(request.name());
        playlist.setDescription(request.description());
        playlist.setCoverImageUrl(request.coverImageUrl());
        playlist.setPublic(request.isPublic());
        playlist = playlistRepository.save(playlist);
        return mapToResponse(playlist);
    }

    @Override
    @Transactional
    public void deletePlaylist(UUID id) {
        if (!playlistRepository.existsById(id)) {
            throw new RuntimeException("Playlist not found");
        }
        playlistRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PlaylistResponse addSongToPlaylist(UUID playlistId, UUID songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (playlist.getSongs() == null) {
            playlist.setSongs(new HashSet<>());
        }
        playlist.getSongs().add(song);
        playlist = playlistRepository.save(playlist);
        return mapToResponse(playlist);
    }

    @Override
    @Transactional
    public PlaylistResponse removeSongFromPlaylist(UUID playlistId, UUID songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (playlist.getSongs() != null) {
            playlist.getSongs().remove(song);
        }
        playlist = playlistRepository.save(playlist);
        return mapToResponse(playlist);
    }


    private PlaylistResponse mapToResponse(Playlist playlist) {
        return new PlaylistResponse(
                playlist.getId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverImageUrl(),
                playlist.isPublic(),
                playlist.getOwnerId()
        );
    }
}
