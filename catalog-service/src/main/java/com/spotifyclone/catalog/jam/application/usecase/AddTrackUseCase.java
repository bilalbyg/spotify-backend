package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.stereotype.Service;

/** Appends a track to the queue, idempotently by key (POC-3). */
@Service
public class AddTrackUseCase {

    private final JamRepository repository;
    private final OutboxRepository outbox;
    private final OptimisticUnitOfWork unitOfWork;

    public AddTrackUseCase(JamRepository repository, OutboxRepository outbox, OptimisticUnitOfWork unitOfWork) {
        this.repository = repository;
        this.outbox = outbox;
        this.unitOfWork = unitOfWork;
    }

    public void execute(JamId sessionId, UserId actor, TrackId track, IdempotencyKey key) {
        unitOfWork.executeWithoutResult(() -> {
            JamSession session = repository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(sessionId));
            session.addTrack(actor, track, key);
            repository.save(session);
            session.drainEvents().forEach(outbox::append);
        });
    }
}
