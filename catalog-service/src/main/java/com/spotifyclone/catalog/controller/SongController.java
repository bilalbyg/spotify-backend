// Dosya Yolu: catalog-service/src/main/java/com/spotifyclone/catalog/controller/SongController.java
package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateSongRequest;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // JSON (@RequestBody) yerine Form Data (MultipartFile) alacak şekilde değiştirdik
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponse createSong(
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        // Tüm işi Service katmanına devrediyoruz
        return songService.createSong(title, artist, audioFile, imageFile);
    }

    @GetMapping("/{id}")
    public SongResponse getSongById(@PathVariable UUID id) {
        return songService.getSongById(id);
    }
}