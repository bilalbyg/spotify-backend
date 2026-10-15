package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamOutboxRepository extends JpaRepository<JamOutboxEntity, UUID> {

    List<JamOutboxEntity> findByProcessedAtIsNullOrderByCreatedAtAsc();

    long countByProcessedAtIsNull();

    long countByProcessedAtIsNotNull();
}
