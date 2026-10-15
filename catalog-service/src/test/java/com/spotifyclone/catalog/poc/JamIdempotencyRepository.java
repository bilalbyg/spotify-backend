package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamIdempotencyRepository
        extends JpaRepository<JamIdempotencyEntity, JamIdempotencyEntity.IdemKey> {
}
