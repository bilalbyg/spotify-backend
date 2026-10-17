package com.spotifyclone.catalog.jam.domain.model;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.exception.InviteLinkInvalidException;
import com.spotifyclone.catalog.jam.domain.exception.NotHostException;
import com.spotifyclone.catalog.jam.domain.exception.NotMemberException;
import com.spotifyclone.catalog.jam.domain.exception.SessionNotActiveException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JamSessionTest {

    private final UserId host = new UserId(UUID.randomUUID());
    private final InviteLink inviteLink = InviteLink.generate();

    private JamSession newSession() {
        return JamSession.create(host, inviteLink);
    }

    @Test
    void create_SetsHostAsMember_Active_EmptyQueue() {
        JamSession session = newSession();

        assertThat(session.status()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(session.host()).isEqualTo(host);
        assertThat(session.members()).containsExactly(host);
        assertThat(session.queue()).isEmpty();
        assertThat(session.currentTrack()).isNull();
    }

    @Test
    void addTrack_AppendsToQueue_AndMakesItCurrent() {
        JamSession session = newSession();
        TrackId track = new TrackId(UUID.randomUUID());

        session.addTrack(host, track, new IdempotencyKey(UUID.randomUUID()));

        assertThat(session.queue()).containsExactly(track);
        assertThat(session.currentTrack()).isEqualTo(track);
    }

    @Test
    void addTrack_IsIdempotentByKey() {
        JamSession session = newSession();
        TrackId track = new TrackId(UUID.randomUUID());
        IdempotencyKey key = new IdempotencyKey(UUID.randomUUID());

        session.addTrack(host, track, key);
        session.addTrack(host, track, key); // same key — no-op

        assertThat(session.queue()).hasSize(1);
    }

    @Test
    void addTrack_DifferentKey_AppendsAgain() {
        JamSession session = newSession();
        session.addTrack(host, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID()));

        assertThat(session.queue()).hasSize(2);
    }

    @Test
    void addTrack_RejectsNonMember() {
        JamSession session = newSession();
        UserId outsider = new UserId(UUID.randomUUID());

        assertThatThrownBy(() ->
                session.addTrack(outsider, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID())))
                .isInstanceOf(NotMemberException.class);
    }

    @Test
    void addTrack_RejectsEndedSession() {
        JamSession session = newSession();
        session.end(host);

        assertThatThrownBy(() ->
                session.addTrack(host, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID())))
                .isInstanceOf(SessionNotActiveException.class);
    }

    @Test
    void castSkipVote_HostSkipsDirectly() {
        JamSession session = newSession();
        TrackId first = new TrackId(UUID.randomUUID());
        TrackId second = new TrackId(UUID.randomUUID());
        session.addTrack(host, first, new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, second, new IdempotencyKey(UUID.randomUUID()));

        session.castSkipVote(host, first);

        assertThat(session.queue()).containsExactly(second);
        assertThat(session.currentTrack()).isEqualTo(second);
    }

    @Test
    void castSkipVote_ThreeMembersReachThreshold_OfFive() {
        JamSession session = newSession();
        UserId m1 = join(session, new UserId(UUID.randomUUID()));
        UserId m2 = join(session, new UserId(UUID.randomUUID()));
        UserId m3 = join(session, new UserId(UUID.randomUUID()));
        join(session, new UserId(UUID.randomUUID())); // 5th member (host + 4)

        TrackId current = new TrackId(UUID.randomUUID());
        TrackId next = new TrackId(UUID.randomUUID());
        session.addTrack(host, current, new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, next, new IdempotencyKey(UUID.randomUUID()));

        // threshold = ceil(5/2) = 3; three non-host votes trigger the skip.
        session.castSkipVote(m1, current);
        session.castSkipVote(m2, current);
        assertThat(session.currentTrack()).isEqualTo(current); // not yet

        session.castSkipVote(m3, current);
        assertThat(session.currentTrack()).isEqualTo(next); // skipped
    }

    @Test
    void castSkipVote_TwoVotesBelowThreshold_DoNotSkip() {
        JamSession session = newSession();
        UserId m1 = join(session, new UserId(UUID.randomUUID()));
        UserId m2 = join(session, new UserId(UUID.randomUUID()));
        join(session, new UserId(UUID.randomUUID()));
        join(session, new UserId(UUID.randomUUID())); // 5 members

        TrackId current = new TrackId(UUID.randomUUID());
        TrackId next = new TrackId(UUID.randomUUID());
        session.addTrack(host, current, new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, next, new IdempotencyKey(UUID.randomUUID()));

        session.castSkipVote(m1, current);
        session.castSkipVote(m2, current); // 2 votes < 3

        assertThat(session.currentTrack()).isEqualTo(current);
    }

    @Test
    void transferHost_RequiresHost_AndMember() {
        JamSession session = newSession();
        UserId newHost = join(session, new UserId(UUID.randomUUID()));

        assertThatThrownBy(() -> session.transferHost(newHost, newHost))
                .isInstanceOf(NotHostException.class);

        session.transferHost(host, newHost);
        assertThat(session.host()).isEqualTo(newHost);
    }

    @Test
    void transferHost_NewHostMustBeMember() {
        JamSession session = newSession();
        UserId outsider = new UserId(UUID.randomUUID());

        assertThatThrownBy(() -> session.transferHost(host, outsider))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be a member");
    }

    @Test
    void end_RequiresHost() {
        JamSession session = newSession();
        UserId outsider = new UserId(UUID.randomUUID());

        assertThatThrownBy(() -> session.end(outsider))
                .isInstanceOf(NotHostException.class);

        session.end(host);
        assertThat(session.status()).isEqualTo(SessionStatus.ENDED);
    }

    @Test
    void join_RequiresMatchingInviteLink() {
        JamSession session = newSession();
        UserId user = new UserId(UUID.randomUUID());

        assertThatThrownBy(() -> session.join(user, InviteLink.generate()))
                .isInstanceOf(InviteLinkInvalidException.class);

        session.join(user, inviteLink);
        assertThat(session.members()).contains(user);
    }

    @Test
    void reorderQueue_MustBePermutation() {
        JamSession session = newSession();
        TrackId a = new TrackId(UUID.randomUUID());
        TrackId b = new TrackId(UUID.randomUUID());
        session.addTrack(host, a, new IdempotencyKey(UUID.randomUUID()));
        session.addTrack(host, b, new IdempotencyKey(UUID.randomUUID()));

        session.reorderQueue(host, List.of(b, a));

        assertThat(session.queue()).containsExactly(b, a);
    }

    @Test
    void drainEvents_CollectsAndClears() {
        JamSession session = newSession();
        session.addTrack(host, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID()));

        List<JamEvent> events = session.drainEvents();

        assertThat(events).anyMatch(e -> e instanceof JamEvent.SessionCreated);
        assertThat(events).anyMatch(e -> e instanceof JamEvent.TrackQueued);
        assertThat(session.drainEvents()).isEmpty(); // cleared after drain
    }

    private UserId join(JamSession session, UserId user) {
        session.join(user, inviteLink);
        return user;
    }
}
