package com.spotifyclone.library.repository;

import com.spotifyclone.library.model.LikedSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, UUID> {
    List<LikedSong> findByUserId(UUID userId);
    boolean existsByUserIdAndSongId(UUID userId, UUID songId);
}
