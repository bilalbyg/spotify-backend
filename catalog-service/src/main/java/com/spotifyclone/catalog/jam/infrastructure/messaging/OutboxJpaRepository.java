package com.spotifyclone.catalog.jam.infrastructure.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Spring Data repository for the jam outbox. */
public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, UUID> {

    List<OutboxJpaEntity> findByProcessedAtIsNullOrderByCreatedAtAsc();
}
