//package com.spotifyclone.library.repository;
//
//import com.spotifyclone.library.model.postgresql.SavedPlaylist;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface SavedPlaylistRepository extends CassandraRepository<SavedPlaylist, UUID> {
//    List<SavedPlaylist> findByUserId(UUID userId);
//    boolean existsByUserIdAndPlaylistId(UUID userId, UUID playlistId);
//}
