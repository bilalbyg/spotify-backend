package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID>, JpaSpecificationExecutor<Song> {

    List<Song> findByAlbumId(UUID albumId);
    List<Song> findByAlbumArtistId(UUID artistId);
}