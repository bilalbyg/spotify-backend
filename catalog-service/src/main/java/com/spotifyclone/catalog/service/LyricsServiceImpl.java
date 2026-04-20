package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.CreateLyricsRequest;
import com.spotifyclone.catalog.dto.LyricsResponse;
import com.spotifyclone.catalog.model.Lyrics;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.LyricsRepository;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LyricsServiceImpl implements LyricsService {

    private final LyricsRepository lyricsRepository;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public LyricsResponse createOrUpdateLyrics(CreateLyricsRequest request) {
        Song song = songRepository.findById(request.songId())
                .orElseThrow(() -> new RuntimeException("Song not found"));

        Optional<Lyrics> existingLyrics = lyricsRepository.findBySongId(request.songId());

        Lyrics lyrics;
        if (existingLyrics.isPresent()) {
            lyrics = existingLyrics.get();
            lyrics.setText(request.text());
        } else {
            lyrics = Lyrics.builder()
                    .text(request.text())
                    .song(song)
                    .build();
        }

        lyrics = lyricsRepository.save(lyrics);
        return mapToResponse(lyrics);
    }

    @Override
    public LyricsResponse getLyricsById(UUID id) {
        Lyrics lyrics = lyricsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lyrics not found"));
        return mapToResponse(lyrics);
    }

    @Override
    public LyricsResponse getLyricsBySongId(UUID songId) {
        Lyrics lyrics = lyricsRepository.findBySongId(songId)
                .orElseThrow(() -> new RuntimeException("Lyrics not found for this song"));
        return mapToResponse(lyrics);
    }

    @Override
    @Transactional
    public void deleteLyrics(UUID id) {
        if (!lyricsRepository.existsById(id)) {
            throw new RuntimeException("Lyrics not found");
        }
        lyricsRepository.deleteById(id);
    }

    private LyricsResponse mapToResponse(Lyrics lyrics) {
        return new LyricsResponse(
                lyrics.getId(),
                lyrics.getText(),
                lyrics.getSong().getId()
        );
    }
}
