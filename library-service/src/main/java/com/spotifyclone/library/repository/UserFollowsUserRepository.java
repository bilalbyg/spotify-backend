package com.spotifyclone.library.repository;

import com.spotifyclone.library.model.UserFollowsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFollowsUserRepository extends JpaRepository<UserFollowsUser, UUID> {
    List<UserFollowsUser> findByFollowerId(UUID followerId);
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
}
