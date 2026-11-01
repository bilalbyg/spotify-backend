package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.jam.application.port.JamMetrics;
import org.springframework.stereotype.Service;

/** Creates a jam session and returns its id and invite link (ADR-006). */
@Service
public class CreateSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final OptimisticUnitOfWork unitOfWork;
    private final JamMetrics metrics;

    public CreateSessionUseCase(
            JamRepository repository,
            OutboxRepository outbox,
            OptimisticUnitOfWork unitOfWork,
            JamMetrics metrics) {
        this.repository = repository;
        this.outbox = outbox;
        this.unitOfWork = unitOfWork;
        this.metrics = metrics;
    }

    public CreateSessionResult execute(UserId host) {
        CreateSessionResult result = unitOfWork.execute(() -> {
            JamSession session = JamSession.create(host, InviteLink.generate());
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
            return new CreateSessionResult(session.id(), session.inviteLink());
        });
        // Counted once, outside the retried unit, so conflict replays don't inflate it.
        metrics.sessionCreated();
        return result;
    }

    public record CreateSessionResult(JamId sessionId, InviteLink inviteLink) {
    }
}
