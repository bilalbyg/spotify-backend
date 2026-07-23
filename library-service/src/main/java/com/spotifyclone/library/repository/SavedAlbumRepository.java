//package com.spotifyclone.library.repository;
//
//import com.spotifyclone.library.model.postgresql.SavedAlbum;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface SavedAlbumRepository extends CassandraRepository<SavedAlbum, UUID> {
//    List<SavedAlbum> findByUserId(UUID userId);
//    boolean existsByUserIdAndAlbumId(UUID userId, UUID albumId);
//}
