package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.LikedSongRequest;
import com.spotifyclone.catalog.dto.LikedSongResponse;

import java.util.List;
import java.util.UUID;

public interface LikedSongService {
    LikedSongResponse likeSong(LikedSongRequest request);
    void unlikeSong(UUID userId, UUID songId);
    List<LikedSongResponse> getLikedSongs(UUID userId);
}
