package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.SavedAlbumRequest;
import com.spotifyclone.catalog.dto.SavedAlbumResponse;

import java.util.List;
import java.util.UUID;

public interface SavedAlbumService {
    SavedAlbumResponse saveAlbum(SavedAlbumRequest request);
    void unsaveAlbum(UUID userId, UUID albumId);
    List<SavedAlbumResponse> getSavedAlbums(UUID userId);
}
