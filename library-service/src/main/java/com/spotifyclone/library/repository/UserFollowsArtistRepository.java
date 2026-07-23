//package com.spotifyclone.library.repository;
//
//import com.spotifyclone.library.model.postgresql.UserFollowsArtist;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface UserFollowsArtistRepository extends CassandraRepository<UserFollowsArtist, UUID> {
//    List<UserFollowsArtist> findByFollowerId(UUID followerId);
//    boolean existsByFollowerIdAndArtistId(UUID followerId, UUID artistId);
//}
