package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.CreateBulkSongRequest;
// Dinamik arama filtrelerinin tek parametre üzerinden taşınması için kriter DTO'su.
import com.spotifyclone.catalog.dto.SongSearchCriteria;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.dto.UpdateSongRequest;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {
    List<SongResponse> getAllSongs();
    SongResponse createSong(CreateSongRequest request); // DTO kullanarak temizledik
    List<SongResponse> createManySongs(UUID albumId, List<CreateBulkSongRequest> requests, List<MultipartFile> audioFiles);
    SongResponse getSongById(UUID id);
    SongResponse updateSong(UUID id, UpdateSongRequest request);
    void deleteSong(UUID id);
    List<SongResponse> getSongsByAlbum(UUID albumId);
    List<SongResponse> getSongsByArtist(UUID artistId); // YENİ
    // Gelişmiş şarkı aramasını kriter objesi ile çalıştırır.
    List<SongResponse> searchSongs(SongSearchCriteria criteria);
}
