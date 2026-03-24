package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {

    List<Song> findByAlbumId(UUID albumId);
    List<Song> findByAlbumArtistId(UUID artistId);
}