package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.jam.application.port.JamMetrics;
import org.springframework.stereotype.Service;

/** Casts a skip vote (ADR-004): host skips directly, members vote to threshold. */
@Service
public class CastSkipVoteUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final OptimisticUnitOfWork unitOfWork;
    private final JamMetrics metrics;

    public CastSkipVoteUseCase(
            JamRepository repository,
            OutboxRepository outbox,
            OptimisticUnitOfWork unitOfWork,
            JamMetrics metrics) {
        this.repository = repository;
        this.outbox = outbox;
        this.unitOfWork = unitOfWork;
        this.metrics = metrics;
    }

    public void execute(JamId sessionId, UserId actor, TrackId track) {
        unitOfWork.executeWithoutResult(() -> {
            JamSession session = repository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(sessionId));
            session.castSkipVote(actor, track);
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
        });
        // Counted once, outside the retried unit, so conflict replays don't inflate it.
        metrics.skipVoted();
    }
}
