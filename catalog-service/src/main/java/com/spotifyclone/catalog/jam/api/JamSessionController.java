package com.spotifyclone.catalog.jam.api;

import com.spotifyclone.catalog.jam.application.usecase.CreateSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.EndSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.JoinSessionUseCase;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST inbound adapter for the jam session lifecycle (create/join/end). The
 * in-session commands (add/skip/reorder/transfer) flow over WebSocket
 * (JamStompController). Actor identity is carried in the request body for now;
 * a later slice should source it from the authenticated principal.
 */
@RestController
@RequestMapping("/api/v1/jam/sessions")
public class JamSessionController {

    private final CreateSessionUseCase createSession;
    private final JoinSessionUseCase joinSession;
    private final EndSessionUseCase endSession;

    public JamSessionController(
            CreateSessionUseCase createSession,
            JoinSessionUseCase joinSession,
            EndSessionUseCase endSession) {
        this.createSession = createSession;
        this.joinSession = joinSession;
        this.endSession = endSession;
    }

    @PostMapping
    public CreateSessionResponse create(@RequestBody CreateSessionRequest request) {
        CreateSessionUseCase.CreateSessionResult result =
                createSession.execute(new UserId(request.hostUserId()));
        return new CreateSessionResponse(result.sessionId().value(), result.inviteLink().value());
    }

    @PostMapping("/{id}/join")
    public void join(@PathVariable UUID id, @RequestBody JoinSessionRequest request) {
        joinSession.execute(
                new JamId(id), new UserId(request.userId()), new InviteLink(request.inviteLink()));
    }

    @PostMapping("/join")
    public void joinByInviteLink(@RequestBody JoinByInviteLinkRequest request) {
        joinSession.executeByInviteLink(
                new InviteLink(request.inviteLink()), new UserId(request.userId()));
    }

    @PostMapping("/{id}/end")
    public void end(@PathVariable UUID id, @RequestBody EndSessionRequest request) {
        endSession.execute(new JamId(id), new UserId(request.userId()));
    }

    public record CreateSessionRequest(UUID hostUserId) {
    }

    public record CreateSessionResponse(UUID sessionId, UUID inviteLink) {
    }

    public record JoinSessionRequest(UUID userId, UUID inviteLink) {
    }

    public record JoinByInviteLinkRequest(UUID userId, UUID inviteLink) {
    }

    public record EndSessionRequest(UUID userId) {
    }
}
