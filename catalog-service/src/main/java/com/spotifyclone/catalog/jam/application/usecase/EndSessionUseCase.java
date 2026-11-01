package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.stereotype.Service;

/** Ends the session (host-only). */
@Service
public class EndSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final OptimisticUnitOfWork unitOfWork;

    public EndSessionUseCase(JamRepository repository, OutboxRepository outbox, OptimisticUnitOfWork unitOfWork) {
        this.repository = repository;
        this.outbox = outbox;
        this.unitOfWork = unitOfWork;
    }

    public void execute(JamId sessionId, UserId actor) {
        unitOfWork.executeWithoutResult(() -> {
            JamSession session = repository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(sessionId));
            session.end(actor);
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
        });
    }
}
