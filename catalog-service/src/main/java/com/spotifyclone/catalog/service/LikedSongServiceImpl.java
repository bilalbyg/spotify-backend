package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.LikedSongRequest;
import com.spotifyclone.catalog.dto.LikedSongResponse;
import com.spotifyclone.catalog.model.LikedSong;
import com.spotifyclone.catalog.model.Song;
import com.spotifyclone.catalog.repository.LikedSongRepository;
import com.spotifyclone.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikedSongServiceImpl implements LikedSongService {

    private final LikedSongRepository likedSongRepository;
    private final SongRepository songRepository;

    @Override
    @Transactional
    public LikedSongResponse likeSong(LikedSongRequest request) {
        if (likedSongRepository.existsByUserIdAndSongId(request.userId(), request.songId())) {
            throw new RuntimeException("Song already liked");
        }

        Song song = songRepository.findById(request.songId())
                .orElseThrow(() -> new RuntimeException("Song not found"));

        LikedSong likedSong = LikedSong.builder()
                .userId(request.userId())
                .song(song)
                .likedAt(LocalDateTime.now())
                .build();

        likedSong = likedSongRepository.save(likedSong);
        return mapToResponse(likedSong);
    }

    @Override
    @Transactional
    public void unlikeSong(UUID userId, UUID songId) {
        LikedSong likedSong = likedSongRepository.findByUserIdAndSongId(userId, songId)
                .orElseThrow(() -> new RuntimeException("Liked song not found"));
        likedSongRepository.delete(likedSong);
    }

    @Override
    public List<LikedSongResponse> getLikedSongs(UUID userId) {
        return likedSongRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LikedSongResponse mapToResponse(LikedSong likedSong) {
        return new LikedSongResponse(
                likedSong.getId(),
                likedSong.getUserId(),
                likedSong.getSong().getId(),
                likedSong.getLikedAt()
        );
    }
}
