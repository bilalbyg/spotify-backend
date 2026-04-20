package com.spotifyclone.auth.repository;

import com.spotifyclone.auth.model.UserFollowsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFollowsUserRepository extends JpaRepository<UserFollowsUser, UUID> {
    List<UserFollowsUser> findByFollowerId(UUID followerId);
    List<UserFollowsUser> findByFollowedId(UUID followedId);
    Optional<UserFollowsUser> findByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
}
