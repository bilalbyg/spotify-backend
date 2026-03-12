// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/service/SongServiceImpl.java
package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.event.SongCreatedEvent;
import com.spotifyclone.catalog.event.SongEventProducer;
import com.spotifyclone.catalog.exception.ResourceNotFoundException;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service // Spring'e bunun bir iş katmanı fasulyesi (bean) olduğunu söyler
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongEventProducer songEventProducer;
    private final FileStorageService fileStorageService;

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
    public SongResponse createSong(String title, String artist, MultipartFile audioFile, MultipartFile imageFile) {

        String audioUrl = fileStorageService.uploadFile(audioFile, "audio");
        String albumImageUrl = "https://example.com/default-cover.jpg";

        if (imageFile != null && !imageFile.isEmpty()) {
            albumImageUrl = fileStorageService.uploadFile(imageFile, "images");
        }

        // 2. VERİTABANI İÇİN ENTITY OLUŞTUR (Gelen stringleri ve MinIO linklerini koy)
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setAlbumImageUrl(albumImageUrl); // MinIO Linki
        song.setAudioUrl(audioUrl);           // MinIO Linki

        // 3. VERİTABANINA KAYDET
        Song savedSong = songRepository.save(song);

        // 4. KAFKA EVENT FIRLATMA
        SongCreatedEvent event = new SongCreatedEvent(
                savedSong.getId(),
                savedSong.getTitle(),
                savedSong.getArtist()
        );
        songEventProducer.sendSongCreatedEvent(event);

        return mapToResponse(savedSong);
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