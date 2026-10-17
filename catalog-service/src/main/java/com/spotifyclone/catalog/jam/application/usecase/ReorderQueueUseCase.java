package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;

import java.util.List;

/** Reorders the queue to a permutation of the current tracks. */
public class ReorderQueueUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;

    public ReorderQueueUseCase(JamRepository repository, OutboxRepository outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    public void execute(JamId sessionId, UserId actor, List<TrackId> ordered) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.reorderQueue(actor, ordered);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
