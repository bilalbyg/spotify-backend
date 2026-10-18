package com.spotifyclone.catalog.jam.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spotifyclone.catalog.jam.application.exception.InviteLinkNotFoundException;
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

    @Transactional

    public void execute(JamId sessionId, UserId user, InviteLink presentedLink) {
        JamSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.join(user, presentedLink);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }

    /** Joins by invite link only — the link resolves to the session (ADR-006). */
    @Transactional
    public void executeByInviteLink(InviteLink presentedLink, UserId user) {
        JamSession session = repository.findByInviteLink(presentedLink)
                .orElseThrow(() -> new InviteLinkNotFoundException(presentedLink));
        session.join(user, presentedLink);
        repository.save(session);
        session.drainEvents().forEach(outbox::append);
    }
}
