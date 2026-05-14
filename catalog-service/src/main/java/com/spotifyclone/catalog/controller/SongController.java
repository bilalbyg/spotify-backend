package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.CreateSongRequest;
// Arama endpoint'inde gelen query parametrelerini tek obje olarak taşımak için kriter DTO'su.
import com.spotifyclone.catalog.dto.SongSearchCriteria;
import com.spotifyclone.catalog.dto.SongResponse;
import com.spotifyclone.catalog.service.SongService;
import lombok.RequiredArgsConstructor;
import com.spotifyclone.catalog.security.annotation.AdminOnly;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // Şarkı oluşturma işlemini sadece ADMIN rolüne açıyoruz.
    @AdminOnly
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> createSong(@ModelAttribute CreateSongRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.createSong(request));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<SongResponse>> getSongsByAlbum(@PathVariable UUID albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable UUID id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<SongResponse>> getSongsByArtist(@PathVariable UUID artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    @GetMapping("/search")
    // Gelişmiş şarkı araması: tüm filtreler opsiyoneldir, dolu olanlar sorguya dahil edilir.
    public ResponseEntity<List<SongResponse>> searchSongs(@RequestParam(required = false) String title,
                                                          // Sanatçı adına göre contains araması yapar.
                                                          @RequestParam(required = false) String artistName,
                                                          // Virgülle ayrılmış genre UUID listesi (örn: ?genreIds=id1,id2).
                                                          @RequestParam(required = false) List<UUID> genreIds,
                                                          // Şarkı süresi alt sınırı (saniye).
                                                          @RequestParam(required = false) Integer minDuration,
                                                          // Şarkı süresi üst sınırı (saniye).
                                                          @RequestParam(required = false) Integer maxDuration,
                                                          // Albüm çıkış yılı alt sınırı.
                                                          @RequestParam(required = false) Integer releaseYearFrom,
                                                          // Albüm çıkış yılı üst sınırı.
                                                          @RequestParam(required = false) Integer releaseYearTo,
                                                          // Bu playlist'te olan şarkıları sonuçtan hariç tutar.
                                                          @RequestParam(required = false) UUID excludePlaylistId) {
        // Controller parametrelerini service'in beklediği tek arama kriteri objesine dönüştürüyoruz.
        SongSearchCriteria criteria = new SongSearchCriteria(
                // Başlık filtresi.
                title,
                // Sanatçı filtresi.
                artistName,
                // Tür (genre) filtreleri.
                genreIds,
                // Minimum süre.
                minDuration,
                // Maksimum süre.
                maxDuration,
                // Başlangıç yılı.
                releaseYearFrom,
                // Bitiş yılı.
                releaseYearTo,
                // Hariç tutulacak playlist.
                excludePlaylistId
        );

        // Kriter nesnesini service katmanına iletip filtrelenmiş sonucu döndürüyoruz.
        return ResponseEntity.ok(songService.searchSongs(criteria));
    }
}
