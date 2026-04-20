package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.UserFollowsArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFollowsArtistRepository extends JpaRepository<UserFollowsArtist, UUID> {
    List<UserFollowsArtist> findByUserId(UUID userId);
    Optional<UserFollowsArtist> findByUserIdAndArtistId(UUID userId, UUID artistId);
    boolean existsByUserIdAndArtistId(UUID userId, UUID artistId);
}
