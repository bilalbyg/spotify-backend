package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateSongRequest;
// Controller'dan gelen tüm arama filtrelerini taşıyan kriter DTO'su.
import com.spotifyclone.catalog.dto.SongSearchCriteria;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.SongRepository;
// Kriter DTO'sunu JPA Specification zincirine çeviren builder.
import com.spotifyclone.catalog.specification.SongSearchSpecificationBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final AlbumService albumService;
    private final FileStorageService fileStorageService;

    @PersistenceContext
    private EntityManager entityManager;

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
        Song song = Song.builder().title(request.title()).duration(request.duration() != null ? request.duration() : 0).audioUrl(audioUrl).album(album).build();

        song = songRepository.save(song);
        return mapToResponse(song);
    }

    @Override
    public SongResponse getSongById(UUID id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new RuntimeException("Şarkı bulunamadı! ID: " + id));
        return mapToResponse(song);
    }

    @Override
    public List<SongResponse> getSongsByAlbum(UUID albumId) {
        return songRepository.findByAlbumId(albumId).stream().map(this::mapToResponse).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<SongResponse> getAllSongs() {
        return songRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<SongResponse> getSongsByArtist(UUID artistId) {
        return songRepository.findByAlbumArtistId(artistId).stream().map(this::mapToResponse).collect(Collectors.toUnmodifiableList());
    }

    @Override
    // Kriter bazlı dinamik filtreleme: sadece dolu parametreler WHERE koşuluna dönüşür.
    public List<SongResponse> searchSongs(SongSearchCriteria criteria) {
        // Builder'dan gelen tek specification ile repository katmanında arama yapıyoruz.
        return songRepository.findAll(
                        // Kriterleri birleştirip çalıştırılabilir sorgu predicatelerine dönüştürür.
                        SongSearchSpecificationBuilder.build(criteria))
                // Domain entity'yi API response DTO'suna mapliyoruz.
                .stream().map(this::mapToResponse).collect(Collectors.toUnmodifiableList());
    }

    private SongResponse mapToResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getDuration(),
                fileStorageService.buildPublicUrl(song.getAudioUrl()),
                song.getAlbum().getId(),
                song.getAlbum().getTitle()
        );
    }
}
