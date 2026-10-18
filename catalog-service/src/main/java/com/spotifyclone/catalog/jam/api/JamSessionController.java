package com.spotifyclone.catalog.jam.api;

import com.spotifyclone.catalog.jam.application.usecase.CreateSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.EndSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.JoinSessionUseCase;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST inbound adapter for the jam session lifecycle (create/join/end). The
 * actor is sourced from the JWT principal (not the request body). In-session
 * commands (add/skip/reorder/transfer) flow over WebSocket (JamStompController).
 */
@RestController
@RequestMapping("/api/v1/jam/sessions")
public class JamSessionController {

    private final CreateSessionUseCase createSession;
    private final JoinSessionUseCase joinSession;
    private final EndSessionUseCase endSession;
    private final FixedWindowRateLimiter rateLimiter;

    public JamSessionController(
            CreateSessionUseCase createSession,
            JoinSessionUseCase joinSession,
            EndSessionUseCase endSession,
            FixedWindowRateLimiter rateLimiter) {
        this.createSession = createSession;
        this.joinSession = joinSession;
        this.endSession = endSession;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping
    public CreateSessionResponse create() {
        UserId host = authenticatedUser();
        CreateSessionUseCase.CreateSessionResult result = createSession.execute(host);
        return new CreateSessionResponse(result.sessionId().value(), result.inviteLink().value());
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinByInviteLink(@RequestBody JoinByInviteLinkRequest request) {
        UserId user = authenticatedUser();
        if (!rateLimiter.allow(user.value().toString())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        joinSession.executeByInviteLink(new InviteLink(request.inviteLink()), user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/end")
    public void end(@PathVariable UUID id) {
        endSession.execute(new JamId(id), authenticatedUser());
    }

    private UserId authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser user)
                || user.userId() == null) {
            throw new IllegalStateException("no authenticated user");
        }
        return new UserId(user.userId());
    }

    public record CreateSessionResponse(UUID sessionId, UUID inviteLink) {
    }

    public record JoinByInviteLinkRequest(UUID inviteLink) {
    }
}
