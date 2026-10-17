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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * STOMP command handler: receives a command on {@code /app/session/{id}/command}
 * and dispatches it to the matching use case. The gateway validates the JWT
 * before proxying (ADR-002); this controller only routes domain commands.
 */
@Controller
public class JamStompController {

    private final AddTrackUseCase addTrack;
    private final ReorderQueueUseCase reorderQueue;
    private final CastSkipVoteUseCase castSkipVote;
    private final TransferHostUseCase transferHost;
    private final EndSessionUseCase endSession;

    public JamStompController(
            AddTrackUseCase addTrack,
            ReorderQueueUseCase reorderQueue,
            CastSkipVoteUseCase castSkipVote,
            TransferHostUseCase transferHost,
            EndSessionUseCase endSession) {
        this.addTrack = addTrack;
        this.reorderQueue = reorderQueue;
        this.castSkipVote = castSkipVote;
        this.transferHost = transferHost;
        this.endSession = endSession;
    }

    @MessageMapping("/session/{sessionId}/command")
    public void command(@DestinationVariable UUID sessionId, @Payload JamCommand command) {
        JamId id = new JamId(sessionId);
        switch (command.type()) {
            case JamCommand.ADD_TRACK -> addTrack.execute(
                    id, new UserId(command.actor()), new TrackId(command.track()),
                    new IdempotencyKey(command.idempotencyKey()));
            case JamCommand.REORDER -> reorderQueue.execute(
                    id, new UserId(command.actor()),
                    command.order().stream().map(TrackId::new).toList());
            case JamCommand.SKIP -> castSkipVote.execute(
                    id, new UserId(command.actor()), new TrackId(command.track()));
            case JamCommand.TRANSFER_HOST -> transferHost.execute(
                    id, new UserId(command.actor()), new UserId(command.newHost()));
            case JamCommand.END -> endSession.execute(id, new UserId(command.actor()));
            default -> throw new IllegalArgumentException("unknown command type: " + command.type());
        }
    }
}
