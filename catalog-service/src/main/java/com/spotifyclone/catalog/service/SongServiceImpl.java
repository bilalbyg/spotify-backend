// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/service/SongServiceImpl.java
package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.exception.ResourceNotFoundException;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service // Spring'e bunun bir iş katmanı fasulyesi (bean) olduğunu söyler
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    public List<SongResponse> getAllSongs() {
        // Veritabanından ham Entity'leri al (Songs)
        List<Song> songs = songRepository.findAll();

        // Onları DTO'ya (SongResponse) dönüştür
        return songs.stream()
                .map(song -> new SongResponse(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getAlbumImageUrl(),
                        song.getAudioUrl()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public SongResponse createSong(CreateSongRequest request) {
        // Gelen DTO'yu (Request) veritabanına kaydetmek için Entity'ye çevir
        Song song = new Song();
        song.setTitle(request.title());
        song.setArtist(request.artist());
        song.setAlbumImageUrl(request.albumImageUrl());
        song.setAudioUrl(request.audioUrl());

        // Veritabanına kaydet
        Song savedSong = songRepository.save(song);

        // Kaydedilen veriyi tekrar DTO (Response) olarak dışarı dön
        return new SongResponse(
                savedSong.getId(),
                savedSong.getTitle(),
                savedSong.getArtist(),
                savedSong.getAlbumImageUrl(),
                savedSong.getAudioUrl()
        );
    }

    @Override
    public SongResponse getSongById(UUID id) {
        return songRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("song.not.found", id));
    }

    private SongResponse mapToResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getAlbumImageUrl(),
                song.getAudioUrl()
        );
    }
}