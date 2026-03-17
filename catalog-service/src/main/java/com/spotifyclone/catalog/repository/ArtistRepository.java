package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    boolean existsByName(String name);
}