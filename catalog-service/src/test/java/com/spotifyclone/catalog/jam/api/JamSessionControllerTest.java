package com.spotifyclone.catalog.jam.api;

import com.spotifyclone.catalog.jam.application.usecase.CreateSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.EndSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.JoinSessionUseCase;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import com.spotifyclone.catalog.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JamSessionControllerTest {

    @Mock
    private CreateSessionUseCase createSession;
    @Mock
    private JoinSessionUseCase joinSession;
    @Mock
    private EndSessionUseCase endSession;
    @Mock
    private FixedWindowRateLimiter rateLimiter;

    private JamSessionController controller;

    @BeforeEach
    void setUp() {
        controller = new JamSessionController(createSession, joinSession, endSession, rateLimiter);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(UUID userId) {
        AuthenticatedUser principal = new AuthenticatedUser(userId, "user@example.com", "USER");
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void create_sourcesHostFromPrincipal() {
        UUID host = UUID.randomUUID();
        JamId sessionId = new JamId(UUID.randomUUID());
        InviteLink inviteLink = InviteLink.generate();
        authenticateAs(host);
        when(createSession.execute(new UserId(host)))
                .thenReturn(new CreateSessionUseCase.CreateSessionResult(sessionId, inviteLink));

        JamSessionController.CreateSessionResponse response = controller.create();

        assertThat(response.sessionId()).isEqualTo(sessionId.value());
        assertThat(response.inviteLink()).isEqualTo(inviteLink.value());
        verify(createSession).execute(new UserId(host));
    }

    @Test
    void joinByInviteLink_sourcesActorFromPrincipal() {
        UUID userId = UUID.randomUUID();
        InviteLink link = InviteLink.generate();
        authenticateAs(userId);
        when(rateLimiter.allow(userId.toString())).thenReturn(true);

        var response = controller.joinByInviteLink(
                new JamSessionController.JoinByInviteLinkRequest(link.value()));

        verify(joinSession).executeByInviteLink(link, new UserId(userId));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void joinByInviteLink_throttled_returns429() {
        UUID userId = UUID.randomUUID();
        InviteLink link = InviteLink.generate();
        authenticateAs(userId);
        when(rateLimiter.allow(userId.toString())).thenReturn(false);

        var response = controller.joinByInviteLink(
                new JamSessionController.JoinByInviteLinkRequest(link.value()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        verify(joinSession, never()).executeByInviteLink(any(), any());
    }

    @Test
    void end_sourcesActorFromPrincipal() {
        UUID userId = UUID.randomUUID();
        JamId id = new JamId(UUID.randomUUID());
        authenticateAs(userId);

        controller.end(id.value());

        verify(endSession).execute(id, new UserId(userId));
    }
}
