package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.SavedPlaylistRequest;
import com.spotifyclone.catalog.dto.SavedPlaylistResponse;

import java.util.List;
import java.util.UUID;

public interface SavedPlaylistService {
    SavedPlaylistResponse savePlaylist(SavedPlaylistRequest request);
    void unsavePlaylist(UUID userId, UUID playlistId);
    List<SavedPlaylistResponse> getSavedPlaylists(UUID userId);
}
