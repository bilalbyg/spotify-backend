package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamSessionRepository extends JpaRepository<JamSessionEntity, UUID> {
}
