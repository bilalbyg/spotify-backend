package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamEventRepository extends JpaRepository<JamEventEntity, Long> {

    List<JamEventEntity> findBySessionIdAndSeqGreaterThanOrderBySeqAsc(UUID sessionId, long seq);
}
