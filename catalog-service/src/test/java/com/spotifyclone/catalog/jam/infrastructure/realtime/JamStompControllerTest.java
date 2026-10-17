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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

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

    @Test
    void addTrack_dispatchesToAddTrackUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        TrackId track = new TrackId(UUID.randomUUID());
        IdempotencyKey key = new IdempotencyKey(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                JamCommand.ADD_TRACK, actor.value(), track.value(), null, null, key.value()));

        verify(addTrack).execute(id, actor, track, key);
    }

    @Test
    void reorder_dispatchesToReorderUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        List<UUID> order = List.of(UUID.randomUUID(), UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                JamCommand.REORDER, actor.value(), null, order, null, null));

        verify(reorderQueue).execute(id, actor, order.stream().map(TrackId::new).toList());
    }

    @Test
    void skip_dispatchesToCastSkipVoteUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        TrackId track = new TrackId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                JamCommand.SKIP, actor.value(), track.value(), null, null, null));

        verify(castSkipVote).execute(id, actor, track);
    }

    @Test
    void transferHost_dispatchesToTransferHostUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());
        UserId newHost = new UserId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                JamCommand.TRANSFER_HOST, actor.value(), null, null, newHost.value(), null));

        verify(transferHost).execute(id, actor, newHost);
    }

    @Test
    void end_dispatchesToEndSessionUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId actor = new UserId(UUID.randomUUID());

        controller.command(id.value(), new JamCommand(
                JamCommand.END, actor.value(), null, null, null, null));

        verify(endSession).execute(id, actor);
    }

    @Test
    void unknownType_throws() {
        JamId id = new JamId(UUID.randomUUID());

        assertThatThrownBy(() -> controller.command(id.value(), new JamCommand(
                "bogus", UUID.randomUUID(), null, null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown command type");
    }
}
