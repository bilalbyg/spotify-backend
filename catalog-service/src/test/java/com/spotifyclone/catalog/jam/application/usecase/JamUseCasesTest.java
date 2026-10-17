package com.spotifyclone.catalog.jam.application.usecase;

import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JamUseCasesTest {

    private final UserId host = new UserId(UUID.randomUUID());

    /** Simple in-memory outbox for asserting published events. */
    private static final class RecordingOutbox implements OutboxRepository {
        final List<JamEvent> events = new ArrayList<>();

        @Override
        public void append(JamEvent event) {
            events.add(event);
        }
    }

    private JamSession sessionFor(JamRepository repository, JamSession session) {
        when(repository.findById(session.id())).thenReturn(Optional.of(session));
        return session;
    }

    @Test
    void createSession_savesAndPublishesSessionCreated() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        CreateSessionUseCase useCase = new CreateSessionUseCase(repository, outbox);

        CreateSessionUseCase.CreateSessionResult result = useCase.execute(host);

        verify(repository).save(any(JamSession.class));
        assertThat(result.sessionId()).isNotNull();
        assertThat(result.inviteLink()).isNotNull();
        assertThat(outbox.events).anyMatch(e -> e instanceof JamEvent.SessionCreated);
    }

    @Test
    void addTrack_appendsToQueueAndPublishesEvent() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        AddTrackUseCase useCase = new AddTrackUseCase(repository, outbox);

        JamSession session = sessionFor(repository, JamSession.create(host, InviteLink.generate()));
        TrackId track = new TrackId(UUID.randomUUID());

        useCase.execute(session.id(), host, track, new IdempotencyKey(UUID.randomUUID()));

        assertThat(session.queue()).containsExactly(track);
        assertThat(outbox.events).anyMatch(e -> e instanceof JamEvent.TrackQueued);
    }

    @Test
    void addTrack_idempotentByKey_publishesSingleEvent() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        AddTrackUseCase useCase = new AddTrackUseCase(repository, outbox);

        JamSession session = sessionFor(repository, JamSession.create(host, InviteLink.generate()));
        TrackId track = new TrackId(UUID.randomUUID());
        IdempotencyKey key = new IdempotencyKey(UUID.randomUUID());

        useCase.execute(session.id(), host, track, key);
        useCase.execute(session.id(), host, track, key);

        assertThat(session.queue()).hasSize(1);
        long queued = outbox.events.stream().filter(e -> e instanceof JamEvent.TrackQueued).count();
        assertThat(queued).isEqualTo(1);
    }

    @Test
    void castSkipVote_hostSkip_publishesSkipped() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        CastSkipVoteUseCase useCase = new CastSkipVoteUseCase(repository, outbox);

        JamSession session = sessionFor(repository, JamSession.create(host, InviteLink.generate()));
        TrackId first = new TrackId(UUID.randomUUID());
        TrackId second = new TrackId(UUID.randomUUID());
        session.addTrack(host, first, new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, second, new IdempotencyKey(UUID.randomUUID()));

        useCase.execute(session.id(), host, first);

        assertThat(session.currentTrack()).isEqualTo(second);
        assertThat(outbox.events).anyMatch(e -> e instanceof JamEvent.Skipped);
    }

    @Test
    void transferHost_publishesHostTransferred() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        TransferHostUseCase useCase = new TransferHostUseCase(repository, outbox);

        JamSession session = sessionFor(repository, JamSession.create(host, InviteLink.generate()));
        UserId newHost = new UserId(UUID.randomUUID());
        session.join(newHost, session.inviteLink());

        useCase.execute(session.id(), host, newHost);

        assertThat(session.host()).isEqualTo(newHost);
        assertThat(outbox.events).anyMatch(e -> e instanceof JamEvent.HostTransferred);
    }

    @Test
    void endSession_publishesSessionEnded() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        EndSessionUseCase useCase = new EndSessionUseCase(repository, outbox);

        JamSession session = sessionFor(repository, JamSession.create(host, InviteLink.generate()));

        useCase.execute(session.id(), host);

        assertThat(session.status()).isEqualTo(com.spotifyclone.catalog.jam.domain.model.SessionStatus.ENDED);
        assertThat(outbox.events).anyMatch(e -> e instanceof JamEvent.SessionEnded);
    }

    @Test
    void useCase_onMissingSession_throwsSessionNotFound() {
        JamRepository repository = mock(JamRepository.class);
        RecordingOutbox outbox = new RecordingOutbox();
        AddTrackUseCase useCase = new AddTrackUseCase(repository, outbox);

        JamId missing = new JamId(UUID.randomUUID());
        when(repository.findById(missing)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(missing, host,
                new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID())))
                .isInstanceOf(SessionNotFoundException.class);
    }
}
