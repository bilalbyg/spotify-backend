package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.LikedSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, UUID> {
    List<LikedSong> findByUserId(UUID userId);
    Optional<LikedSong> findByUserIdAndSongId(UUID userId, UUID songId);
    boolean existsByUserIdAndSongId(UUID userId, UUID songId);
}
