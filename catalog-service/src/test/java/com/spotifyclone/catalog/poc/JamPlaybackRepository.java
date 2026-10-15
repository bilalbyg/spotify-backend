package com.spotifyclone.catalog.poc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * POC fixture repository. NOT production code.
 */
public interface JamPlaybackRepository extends JpaRepository<JamPlaybackEntity, UUID> {
}
