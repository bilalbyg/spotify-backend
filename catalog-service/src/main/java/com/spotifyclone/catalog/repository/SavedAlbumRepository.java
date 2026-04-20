package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.SavedAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedAlbumRepository extends JpaRepository<SavedAlbum, UUID> {
    List<SavedAlbum> findByUserId(UUID userId);
    Optional<SavedAlbum> findByUserIdAndAlbumId(UUID userId, UUID albumId);
    boolean existsByUserIdAndAlbumId(UUID userId, UUID albumId);
}
