package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.SavedPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedPlaylistRepository extends JpaRepository<SavedPlaylist, UUID> {
    List<SavedPlaylist> findByUserId(UUID userId);
    Optional<SavedPlaylist> findByUserIdAndPlaylistId(UUID userId, UUID playlistId);
    boolean existsByUserIdAndPlaylistId(UUID userId, UUID playlistId);
}
