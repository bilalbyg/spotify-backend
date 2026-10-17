package com.spotifyclone.catalog.jam.application.port;

import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;

import java.util.Optional;

/**
 * Outbound port for persisting the Jam aggregate. The Phase-3 persistence
 * adapter (JPA + PostgreSQL) implements this; the application layer and
 * domain know only this interface.
 */
public interface JamRepository {

    Optional<JamSession> findById(JamId id);

    void save(JamSession session);
}
