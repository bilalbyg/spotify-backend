package com.spotifyclone.library.repository;

import com.spotifyclone.library.model.SavedPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedPlaylistRepository extends JpaRepository<SavedPlaylist, UUID> {
    List<SavedPlaylist> findByUserId(UUID userId);
    boolean existsByUserIdAndPlaylistId(UUID userId, UUID playlistId);
}
