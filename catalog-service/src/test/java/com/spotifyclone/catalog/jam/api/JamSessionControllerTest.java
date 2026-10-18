package com.spotifyclone.catalog.jam.api;

import com.spotifyclone.catalog.jam.application.usecase.CreateSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.EndSessionUseCase;
import com.spotifyclone.catalog.jam.application.usecase.JoinSessionUseCase;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

    @Test
    void create_returnsSessionIdAndInviteLink() {
        UUID host = UUID.randomUUID();
        JamId sessionId = new JamId(UUID.randomUUID());
        InviteLink inviteLink = InviteLink.generate();
        when(createSession.execute(new UserId(host)))
                .thenReturn(new CreateSessionUseCase.CreateSessionResult(sessionId, inviteLink));

        JamSessionController.CreateSessionResponse response =
                controller.create(new JamSessionController.CreateSessionRequest(host));

        assertThat(response.sessionId()).isEqualTo(sessionId.value());
        assertThat(response.inviteLink()).isEqualTo(inviteLink.value());
    }

    @Test
    void join_dispatchesToJoinUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId user = new UserId(UUID.randomUUID());
        InviteLink link = InviteLink.generate();

        controller.join(id.value(),
                new JamSessionController.JoinSessionRequest(user.value(), link.value()));

        verify(joinSession).execute(id, user, link);
    }

    @Test
    void joinByInviteLink_dispatchesToInviteLinkJoin() {
        UserId user = new UserId(UUID.randomUUID());
        InviteLink link = InviteLink.generate();
        when(rateLimiter.allow(user.value().toString())).thenReturn(true);

        var response = controller.joinByInviteLink(
                new JamSessionController.JoinByInviteLinkRequest(user.value(), link.value()));

        verify(joinSession).executeByInviteLink(link, user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void joinByInviteLink_throttled_returns429() {
        UserId user = new UserId(UUID.randomUUID());
        InviteLink link = InviteLink.generate();
        when(rateLimiter.allow(user.value().toString())).thenReturn(false);

        var response = controller.joinByInviteLink(
                new JamSessionController.JoinByInviteLinkRequest(user.value(), link.value()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        verify(joinSession, never()).executeByInviteLink(any(), any());
    }

    @Test
    void end_dispatchesToEndUseCase() {
        JamId id = new JamId(UUID.randomUUID());
        UserId user = new UserId(UUID.randomUUID());

        controller.end(id.value(), new JamSessionController.EndSessionRequest(user.value()));

        verify(endSession).execute(id, user);
    }
}
