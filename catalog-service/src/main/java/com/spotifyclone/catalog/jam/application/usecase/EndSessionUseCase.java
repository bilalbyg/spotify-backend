package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.stereotype.Service;
import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;

/** Ends the session (host-only). */
@Service
public class EndSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;

    public EndSessionUseCase(JamRepository repository, OutboxRepository outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    public void execute(JamId sessionId, UserId actor) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.end(actor);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
