package com.spotifyclone.catalog.jam.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Spring Data repository for the jam session entity. */
public interface JamSessionJpaRepository extends JpaRepository<JamSessionJpaEntity, UUID> {
}
