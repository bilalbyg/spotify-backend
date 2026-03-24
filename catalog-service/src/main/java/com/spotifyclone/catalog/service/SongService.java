package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;

import java.util.List;
import java.util.UUID;

public interface SongService {
    List<SongResponse> getAllSongs();
    SongResponse createSong(CreateSongRequest request); // DTO kullanarak temizledik
    SongResponse getSongById(UUID id);
    List<SongResponse> getSongsByAlbum(UUID albumId);
    List<SongResponse> getSongsByArtist(UUID artistId); // YENİ
}