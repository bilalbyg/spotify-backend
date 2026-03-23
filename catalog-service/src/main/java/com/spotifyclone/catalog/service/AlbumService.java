package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.AlbumResponse;
import com.spotifyclone.catalog.dto.CreateAlbumRequest;
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService; // REPO GİTTİ, SERVİS GELDİ!
    private final FileStorageService fileStorageService;

    public AlbumResponse createAlbum(CreateAlbumRequest request) {
        // 1. Sanatçıyı artık kendi Repository'sinden değil, ArtistService üzerinden istiyoruz
        Artist artist = artistService.getArtistById(request.artistId());

        // 2. Bu sanatçının zaten bu isimde bir albümü var mı kontrol et
        if (albumRepository.existsByTitleAndArtistId(request.title(), request.artistId())) {
            throw new RuntimeException("Bu sanatçının '" + request.title() + "' adında bir albümü zaten var.");
        }

        // 3. Kapak fotoğrafını MinIO'ya yükle
        String uploadedImageUrl = null;
        if (request.image() != null && !request.image().isEmpty()) {
            uploadedImageUrl = fileStorageService.uploadFile(request.image(), "albums");
        }

        // 4. Albümü oluştur ve kaydet
        Album album = Album.builder()
                .title(request.title())
                .releaseYear(request.releaseYear())
                .coverImageUrl(uploadedImageUrl)
                .artist(artist)
                .build();

        album = albumRepository.save(album);
        return mapToResponse(album);
    }

    public List<AlbumResponse> getAlbumsByArtist(UUID artistId) {
        return albumRepository.findByArtistId(artistId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AlbumResponse mapToResponse(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseYear(),
                album.getCoverImageUrl(),
                album.getArtist().getId(),
                album.getArtist().getName()
        );
    }
}