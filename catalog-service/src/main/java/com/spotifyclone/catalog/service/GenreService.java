package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateGenreRequest;
import com.spotifyclone.catalog.dto.GenreResponse;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    List<GenreResponse> getAllGenres();
    GenreResponse createGenre(CreateGenreRequest request);
    GenreResponse getGenreById(UUID id);
    GenreResponse updateGenre(UUID id, CreateGenreRequest request);
    void deleteGenre(UUID id);
}
