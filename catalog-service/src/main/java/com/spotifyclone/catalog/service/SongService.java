// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/service/SongService.java
package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import java.util.List;

public interface SongService {
    List<SongResponse> getAllSongs();
    SongResponse createSong(CreateSongRequest request);
}