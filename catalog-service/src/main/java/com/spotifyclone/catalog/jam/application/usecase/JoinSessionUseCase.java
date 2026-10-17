package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.stereotype.Service;
import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;

/** Joins a session by presenting a matching invite link (ADR-006). */
@Service
public class JoinSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;

    public JoinSessionUseCase(JamRepository repository, OutboxRepository outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    public void execute(JamId sessionId, UserId user, InviteLink presentedLink) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.join(user, presentedLink);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
