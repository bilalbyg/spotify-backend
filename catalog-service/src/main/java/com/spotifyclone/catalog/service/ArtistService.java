package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateArtistRequest;
import com.spotifyclone.catalog.dto.CreateBulkArtistRequest;
import com.spotifyclone.catalog.dto.ArtistResponse;
import com.spotifyclone.catalog.dto.UpdateArtistRequest;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.repository.AlbumRepository;
import com.spotifyclone.catalog.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
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

    @Transactional
    public List<ArtistResponse> createManyArtists(List<CreateBulkArtistRequest> requests, List<MultipartFile> images) {
        List<ArtistResponse> responses = new ArrayList<>();

        for (CreateBulkArtistRequest request : requests) {
            if (artistRepository.existsByName(request.name())) {
                throw new RuntimeException("Bu sanatçı zaten mevcut: " + request.name());
            }
        }

        for (int i = 0; i < requests.size(); i++) {
            CreateBulkArtistRequest request = requests.get(i);

            String uploadedImageUrl = null;
            Integer imageIndex = request.imageIndex();
            if (imageIndex != null && imageIndex >= 0 && imageIndex < images.size()) {
                MultipartFile image = images.get(imageIndex);
                if (image != null && !image.isEmpty()) {
                    uploadedImageUrl = fileStorageService.uploadFile(image, "images");
                }
            }

            Artist artist = Artist.builder()
                    .name(request.name())
                    .bio(request.bio())
                    .imageUrl(uploadedImageUrl)
                    .build();

            artist = artistRepository.save(artist);
            responses.add(mapToResponse(artist));
        }

        return responses;
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

    public ArtistResponse getArtistResponseById(UUID id) {
        return mapToResponse(getArtistById(id));
    }

    public ArtistResponse updateArtist(UUID id, UpdateArtistRequest request) {
        Artist artist = getArtistById(id);

        if (request.name() != null && !request.name().isBlank()) {
            if (!request.name().equals(artist.getName()) && artistRepository.existsByName(request.name())) {
                throw new RuntimeException("Bu sanatçı adı zaten mevcut: " + request.name());
            }
            artist.setName(request.name());
        }
        if (request.bio() != null) {
            artist.setBio(request.bio());
        }

        artist = artistRepository.save(artist);
        return mapToResponse(artist);
    }

    public void deleteArtist(UUID id) {
        Artist artist = getArtistById(id);
        if (albumRepository.existsByArtistId(id)) {
            throw new RuntimeException("Bu sanatçının albümleri var. Önce albümlerini silmelisiniz.");
        }
        artistRepository.delete(artist);
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
