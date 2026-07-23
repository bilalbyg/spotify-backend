package com.spotifyclone.library.repository;

import com.spotifyclone.library.model.LikedSong;
import com.spotifyclone.library.model.LikedSongKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikedSongRepository extends CassandraRepository<LikedSong, LikedSongKey> {
    List<LikedSong> findByKeyUserId(UUID userId);
    boolean existsByKeyUserIdAndKeySongId(UUID userId, UUID songId);
    void deleteByKeyUserIdAndKeySongId(UUID userId, UUID songId);
}
