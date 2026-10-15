package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamVoteRepository extends JpaRepository<JamVoteEntity, JamVoteEntity.VoteKey> {

    long countBySessionIdAndTrackId(UUID sessionId, UUID trackId);

    boolean existsBySessionIdAndTrackIdAndUserId(UUID sessionId, UUID trackId, UUID userId);
}
