package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.jam.infrastructure.metrics.JamMetrics;

/** Creates a jam session and returns its id and invite link (ADR-006). */
@Service
public class CreateSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final JamMetrics metrics;

    public CreateSessionUseCase(JamRepository repository, OutboxRepository outbox, JamMetrics metrics) {
        this.repository = repository;
        this.outbox = outbox;
        this.metrics = metrics;
    }

    @Transactional

    public CreateSessionResult execute(UserId host) {
        JamSession session = JamSession.create(host, InviteLink.generate());
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
        metrics.sessionCreated();
        return new CreateSessionResult(session.id(), session.inviteLink());
    }

    public record CreateSessionResult(JamId sessionId, InviteLink inviteLink) {
    }
}
