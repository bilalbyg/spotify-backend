package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.AlbumResponse;
import com.spotifyclone.catalog.dto.CreateAlbumRequest;
import com.spotifyclone.catalog.model.Album;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.repository.AlbumRepository;
import com.spotifyclone.catalog.specification.AlbumSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
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
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AlbumResponse> searchAlbumsByTitle(String title) {
        return searchAlbums(title, null, null, null);
    }

    public List<AlbumResponse> searchAlbumsByArtistName(String artistName) {
        return searchAlbums(null, artistName, null, null);
    }

    public List<AlbumResponse> searchAlbumsByReleaseYearRange(Integer releaseYearFrom, Integer releaseYearTo) {
        return searchAlbums(null, null, releaseYearFrom, releaseYearTo);
    }

    public List<AlbumResponse> searchAlbumsByReleaseYearFrom(Integer releaseYearFrom) {
        return searchAlbums(null, null, releaseYearFrom, null);
    }

    public List<AlbumResponse> searchAlbumsByReleaseYearTo(Integer releaseYearTo) {
        return searchAlbums(null, null, null, releaseYearTo);
    }

    public List<AlbumResponse> searchAlbumsByTitleAndArtistName(String title, String artistName) {
        return searchAlbums(title, artistName, null, null);
    }

    public List<AlbumResponse> searchAlbumsByTitleAndReleaseYearRange(String title,
                                                                      Integer releaseYearFrom,
                                                                      Integer releaseYearTo) {
        return searchAlbums(title, null, releaseYearFrom, releaseYearTo);
    }

    public List<AlbumResponse> searchAlbumsByArtistNameAndReleaseYearRange(String artistName,
                                                                           Integer releaseYearFrom,
                                                                           Integer releaseYearTo) {
        return searchAlbums(null, artistName, releaseYearFrom, releaseYearTo);
    }

    public List<AlbumResponse> searchAlbums(String title,
                                            String artistName,
                                            Integer releaseYearFrom,
                                            Integer releaseYearTo) {
        Specification<Album> spec = Specification.where(AlbumSpecifications.titleContains(title))
                .and(AlbumSpecifications.artistNameContains(artistName))
                .and(AlbumSpecifications.releaseYearGreaterThanOrEqual(releaseYearFrom))
                .and(AlbumSpecifications.releaseYearLessThanOrEqual(releaseYearTo));

        return albumRepository.findAll(spec)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public Album getAlbumById(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Albüm bulunamadı! ID: " + id));
        return album;
    }

    // Frontend'e albüm detaylarını dönmek için (Entity yerine DTO döner)
    public AlbumResponse getAlbumResponseById(UUID id) {
        Album album = getAlbumById(id); // Zaten var olan Entity bulucu metodunu kullanıyoruz
        return mapToResponse(album);    // DTO'ya çevirip yolluyoruz
    }

    private AlbumResponse mapToResponse(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseYear(),
                fileStorageService.buildPublicUrl(album.getCoverImageUrl()),
                album.getArtist().getId(),
                album.getArtist().getName()
        );
    }
}
