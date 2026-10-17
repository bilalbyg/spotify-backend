package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;

/** Appends a track to the queue, idempotently by key (POC-3). */
public class AddTrackUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;

    public AddTrackUseCase(JamRepository repository, OutboxRepository outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    public void execute(JamId sessionId, UserId actor, TrackId track, IdempotencyKey key) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.addTrack(actor, track, key);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
