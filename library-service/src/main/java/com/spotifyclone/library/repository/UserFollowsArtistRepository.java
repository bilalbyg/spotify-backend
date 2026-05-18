package com.spotifyclone.library.repository;

import com.spotifyclone.library.model.UserFollowsArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFollowsArtistRepository extends JpaRepository<UserFollowsArtist, UUID>, JpaSpecificationExecutor<UserFollowsArtist> {
    List<UserFollowsArtist> findByFollowerId(UUID followerId);
    boolean existsByFollowerIdAndArtistId(UUID followerId, UUID artistId);
}
