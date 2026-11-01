package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.InviteLinkNotFoundException;
import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.stereotype.Service;

/** Joins a session by presenting a matching invite link (ADR-006). */
@Service
public class JoinSessionUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final OptimisticUnitOfWork unitOfWork;

    public JoinSessionUseCase(JamRepository repository, OutboxRepository outbox, OptimisticUnitOfWork unitOfWork) {
        this.repository = repository;
        this.outbox = outbox;
        this.unitOfWork = unitOfWork;
    }

    public void execute(JamId sessionId, UserId user, InviteLink presentedLink) {
        unitOfWork.executeWithoutResult(() -> {
            JamSession session = repository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(sessionId));
            session.join(user, presentedLink);
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
        });
    }

    /** Joins by invite link only — the link resolves to the session (ADR-006). */
    public JamId executeByInviteLink(InviteLink presentedLink, UserId user) {
        return unitOfWork.execute(() -> {
            JamSession session = repository.findByInviteLink(presentedLink)
                    .orElseThrow(() -> new InviteLinkNotFoundException(presentedLink));
            session.join(user, presentedLink);
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
            return session.id();
        });
    }
}
