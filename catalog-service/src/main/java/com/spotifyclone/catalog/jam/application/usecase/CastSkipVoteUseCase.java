package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;

/** Casts a skip vote (ADR-004): host skips directly, members vote to threshold. */
public class CastSkipVoteUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;

    public CastSkipVoteUseCase(JamRepository repository, OutboxRepository outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    public void execute(JamId sessionId, UserId actor, TrackId track) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.castSkipVote(actor, track);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
