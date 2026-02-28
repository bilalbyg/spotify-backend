// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/controller/SongController.java
package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello! Catalog Service is up and Gateway routing success!";
    }

    @GetMapping
    public List<SongResponse> getAllSongs() {
        return songService.getAllSongs();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponse createSong(@Valid @RequestBody CreateSongRequest request) {
        return songService.createSong(request);
    }

    @GetMapping("/{id}")
    public SongResponse getSongById(@PathVariable UUID id) {
        return songService.getSongById(id);
    }
}