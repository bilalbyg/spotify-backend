package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID>, JpaSpecificationExecutor<Album> {

    List<Album> findByArtistId(UUID artistId);

    boolean existsByTitleAndArtistId(String title, UUID artistId);
}
