package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final AlbumService albumService;
    private final FileStorageService fileStorageService;

    @Override
    public SongResponse createSong(CreateSongRequest request) {
        // 1. Albümü bul (Eğer albüm yoksa hata fırlatır)
        Album album = albumService.getAlbumById(request.albumId());

        // 2. Ses dosyasını MinIO'ya yükle (Zorunlu)
        if (request.audioFile() == null || request.audioFile().isEmpty()) {
            throw new RuntimeException("Ses dosyası yüklemek zorunludur!");
        }
        String audioUrl = fileStorageService.uploadFile(request.audioFile(), "songs");

        // 3. Şarkıyı kaydet
        Song song = Song.builder()
                .title(request.title())
                .duration(request.duration() != null ? request.duration() : 0)
                .audioUrl(audioUrl)
                .album(album)
                .build();

        song = songRepository.save(song);
        return mapToResponse(song);
    }

    @Override
    public SongResponse getSongById(UUID id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şarkı bulunamadı! ID: " + id));
        return mapToResponse(song);
    }

    @Override
    public List<SongResponse> getSongsByAlbum(UUID albumId) {
        return songRepository.findByAlbumId(albumId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<SongResponse> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SongResponse> getSongsByArtist(UUID artistId) {
        return songRepository.findByAlbumArtistId(artistId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SongResponse mapToResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getDuration(),
                song.getAudioUrl(),
                song.getAlbum().getId(),
                song.getAlbum().getTitle()
        );
    }
}