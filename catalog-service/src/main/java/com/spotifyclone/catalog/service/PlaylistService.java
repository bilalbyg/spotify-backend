package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreatePlaylistRequest;
import com.spotifyclone.catalog.dto.PlaylistResponse;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {
    List<PlaylistResponse> getAllPlaylists();
    PlaylistResponse createPlaylist(CreatePlaylistRequest request);
    PlaylistResponse getPlaylistById(UUID id);
    PlaylistResponse updatePlaylist(UUID id, CreatePlaylistRequest request);
    void deletePlaylist(UUID id);
    PlaylistResponse addSongToPlaylist(UUID playlistId, UUID songId);
    PlaylistResponse removeSongFromPlaylist(UUID playlistId, UUID songId);
}
