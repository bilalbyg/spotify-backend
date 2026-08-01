package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateArtistRequest;
import com.spotifyclone.catalog.dto.ArtistResponse;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final FileStorageService fileStorageService;

    public ArtistResponse createArtist(CreateArtistRequest request) {
        if (artistRepository.existsByName(request.name())) {
            throw new RuntimeException("Bu sanatçı zaten mevcut: " + request.name());
        }

        String uploadedImageUrl = null;
        if (request.image() != null && !request.image().isEmpty()) {
            uploadedImageUrl = fileStorageService.uploadFile(request.image(), "images");
        }

        Artist artist = Artist.builder()
                .name(request.name())
                .bio(request.bio())
                .imageUrl(uploadedImageUrl)
                .build();

        artist = artistRepository.save(artist);
        return mapToResponse(artist);
    }

    public List<ArtistResponse> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Artist getArtistById(UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sanatçı bulunamadı! ID: " + id));
    }

    private ArtistResponse mapToResponse(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getBio(),
                fileStorageService.buildPublicUrl(artist.getImageUrl()),
                artist.getPopularity()
        );
    }
}
