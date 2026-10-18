package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.jam.infrastructure.metrics.JamMetrics;

/** Casts a skip vote (ADR-004): host skips directly, members vote to threshold. */
@Service
public class CastSkipVoteUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final JamMetrics metrics;

    public CastSkipVoteUseCase(JamRepository repository, OutboxRepository outbox, JamMetrics metrics) {
        this.repository = repository;
        this.outbox = outbox;
        this.metrics = metrics;
    }

    @Transactional

    public void execute(JamId sessionId, UserId actor, TrackId track) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.castSkipVote(actor, track);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
        metrics.skipVoted();
    }
}
