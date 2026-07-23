package com.spotifyclone.library.service;

import com.spotifyclone.library.model.LikedSong;
import com.spotifyclone.library.model.LikedSongKey;
import com.spotifyclone.library.repository.LikedSongRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongService {

    private LikedSongRepository likedSongRepository;

    public void likeSong(UUID userId, UUID songId) {
        LikedSongKey key = LikedSongKey.builder()
                .userId(userId)
                .songId(songId)
                .build();

        LikedSong likedSong = LikedSong.builder()
                .key(key)
                .likedAt(Instant.now())
                .build();

        likedSongRepository.save(likedSong);
    }

    public void unlikeSong(UUID userId, UUID songId) {
        likedSongRepository.deleteByKeyUserIdAndKeySongId(userId, songId);
    }

    public boolean isLiked(UUID userId, UUID songId) {
        return likedSongRepository.existsByKeyUserIdAndKeySongId(userId, songId);
    }

    public List<LikedSong> getLikedSongs(UUID userId) {
        return likedSongRepository.findByKeyUserId(userId);
    }
}
