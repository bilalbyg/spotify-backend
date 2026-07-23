//package com.spotifyclone.library.repository;
//
//import com.spotifyclone.library.model.postgresql.UserFollowsUser;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface UserFollowsUserRepository extends CassandraRepository<UserFollowsUser, UUID> {
//    List<UserFollowsUser> findByFollowerId(UUID followerId);
//    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
//}
