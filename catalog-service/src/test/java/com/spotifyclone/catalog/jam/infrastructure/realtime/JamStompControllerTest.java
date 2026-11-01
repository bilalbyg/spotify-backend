package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.jam.application.usecase.AddTrackUseCase;
import com.spotifyclone.catalog.jam.application.usecase.CastSkipVoteUseCase;
import com.spotifyclone.catalog.jam.application.usecase.EndSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.ReorderQueueUseCase;
import com.spotifyclone.catalog.jam.application.usecase.TransferHostUseCase;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JamStompControllerTest {

    @Mock
    private AddTrackUseCase addTrack;
    @Mock
    private ReorderQueueUseCase reorderQueue;
    @Mock
    private CastSkipVoteUseCase castSkipVote;
    @Mock
    private TransferHostUseCase transferHost;
    @Mock
    private EndSessionUseCase endSession;

    private JamStompController controller;

    @BeforeEach
    void setUp() {
        controller = new JamStompController(addTrack, reorderQueue, castSkipVote, transferHost, endSession);
    }

    /** The actor is the handshake-authenticated user from the STOMP session, never the payload. */
    private SimpMessageHeaderAccessor sessionWithUser(UUID userId) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setSessionAttributes(Map.of(JamHandshakeInterceptor.USER_ID_ATTRIBUTE, userId.toString()));
        return headers;
    }

    @Test
    void addTrack_dispatchesWithActorFromSession() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        TrackId track = new TrackId(UUID.randomUUID());
        IdempotencyKey key = new IdempotencyKey(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                        JamCommand.ADD_TRACK, track.value(), null, null, key.value()),
                sessionWithUser(actor.value()));

        verify(addTrack).execute(id, actor, track, key);
    }

    @Test
    void reorder_dispatchesWithActorFromSession() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        List<UUID> order = List.of(UUID.randomUUID(), UUID.randomUUID());

        controller.command(id.value(), new JamCommand(JamCommand.REORDER, null, order, null, null),
                sessionWithUser(actor.value()));

        verify(reorderQueue).execute(id, actor, order.stream().map(TrackId::new).toList());
    }

    @Test
    void skip_dispatchesWithActorFromSession() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        TrackId track = new TrackId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(JamCommand.SKIP, track.value(), null, null, null),
                sessionWithUser(actor.value()));

        verify(castSkipVote).execute(id, actor, track);
    }

    @Test
    void transferHost_dispatchesWithActorFromSession() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        UserId newHost = new UserId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(JamCommand.TRANSFER_HOST, null, null, newHost.value(), null),
                sessionWithUser(actor.value()));

        verify(transferHost).execute(id, actor, newHost);
    }

    @Test
    void end_dispatchesWithActorFromSession() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(JamCommand.END, null, null, null, null),
                sessionWithUser(actor.value()));

        verify(endSession).execute(id, actor);
    }

    @Test
    void missingSessionIdentity_rejectsCommand() {
        JamId id = new JamId(UUID.randomUUID());
        SimpMessageHeaderAccessor noUser = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        noUser.setSessionAttributes(Map.of());

        assertThatThrownBy(() -> controller.command(id.value(),
                new JamCommand(JamCommand.END, null, null, null, null), noUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("authenticated");

        verifyNoInteractions(addTrack, reorderQueue, castSkipVote, transferHost, endSession);
    }

    @Test
    void unknownType_throws() {
        JamId id = new JamId(UUID.randomUUID());

        assertThatThrownBy(() -> controller.command(id.value(),
                new JamCommand("bogus", null, null, null, null), sessionWithUser(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown command type");
    }
}
