package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LyricsRepository extends JpaRepository<Lyrics, UUID> {
    Optional<Lyrics> findBySongId(UUID songId);
}
