package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateGenreRequest;
import com.spotifyclone.catalog.dto.GenreResponse;
import com.spotifyclone.catalog.model.Genre;
import com.spotifyclone.catalog.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreResponse createGenre(CreateGenreRequest request) {
        Genre genre = Genre.builder()
                .name(request.name())
                .iconUrl(request.iconUrl())
                .build();
        genre = genreRepository.save(genre);
        return mapToResponse(genre);
    }

    @Override
    public GenreResponse getGenreById(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        return mapToResponse(genre);
    }

    @Override
    @Transactional
    public GenreResponse updateGenre(UUID id, CreateGenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        genre.setName(request.name());
        genre.setIconUrl(request.iconUrl());
        genre = genreRepository.save(genre);
        return mapToResponse(genre);
    }

    @Override
    @Transactional
    public void deleteGenre(UUID id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found");
        }
        genreRepository.deleteById(id);
    }

    private GenreResponse mapToResponse(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName(), genre.getIconUrl());
    }
}
