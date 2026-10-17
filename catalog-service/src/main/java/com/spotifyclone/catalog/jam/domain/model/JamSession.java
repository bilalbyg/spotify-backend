package com.spotifyclone.catalog.jam.domain.model;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.exception.InviteLinkInvalidException;
import com.spotifyclone.catalog.jam.domain.exception.NotHostException;
import com.spotifyclone.catalog.jam.domain.exception.NotMemberException;
import com.spotifyclone.catalog.jam.domain.exception.SessionNotActiveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The Jam session aggregate root. Encodes the business rules proven in the
 * Phase-1 POCs:
 *
 * <ul>
 *   <li>queue head is the current track; append is idempotent by key (POC-3)</li>
 *   <li>skip vote is hybrid: host skips directly, members vote to reach
 *       {@code ceil(members/2)} (ADR-004, POC-2)</li>
 *   <li>transfer-host and end are guarded, host-only operations (POC-2)</li>
 *   <li>skip is atomic — head removal and current advancement stay consistent
 *       (POC-7)</li>
 * </ul>
 *
 * <p>Pure domain object: no persistence or transport concerns. The version for
 * optimistic locking lives in the Phase-3 persistence adapter, not here.
 */
public final class JamSession {

    private final JamId id;
    private UserId host;
    private final Set<UserId> members;
    private final List<TrackId> queue;
    private SessionStatus status;
    private final InviteLink inviteLink;
    private final Set<IdempotencyKey> processedKeys;
    private final Map<TrackId, Set<UserId>> skipVotes;
    private final List<JamEvent> events;

    private JamSession(
            JamId id,
            UserId host,
            Set<UserId> members,
            List<TrackId> queue,
            SessionStatus status,
            InviteLink inviteLink,
            Set<IdempotencyKey> processedKeys,
            Map<TrackId, Set<UserId>> skipVotes) {
        this.id = id;
        this.host = host;
        this.members = members;
        this.queue = queue;
        this.status = status;
        this.inviteLink = inviteLink;
        this.processedKeys = processedKeys;
        this.skipVotes = skipVotes;
        this.events = new ArrayList<>();
    }

    public static JamSession create(UserId host, InviteLink inviteLink) {
        JamSession session = new JamSession(
                new JamId(UUID.randomUUID()),
                host,
                new LinkedHashSet<>(),
                new ArrayList<>(),
                SessionStatus.ACTIVE,
                inviteLink,
                new HashSet<>(),
                new HashMap<>());
        session.members.add(host);
        session.events.add(new JamEvent.SessionCreated(session.id, host, inviteLink));
        return session;
    }

    /**
     * Reconstructs an existing aggregate from persisted state (used by the
     * persistence adapter). Does not emit events and starts with an empty
     * event log.
     */
    public static JamSession reconstitute(
            JamId id,
            UserId host,
            Set<UserId> members,
            List<TrackId> queue,
            SessionStatus status,
            InviteLink inviteLink,
            Set<IdempotencyKey> processedKeys,
            Map<TrackId, Set<UserId>> skipVotes) {
        return new JamSession(
                id,
                host,
                new LinkedHashSet<>(members),
                new ArrayList<>(queue),
                status,
                inviteLink,
                new HashSet<>(processedKeys),
                new HashMap<>(skipVotes));
    }

    /** Joins a session by presenting the invite link (ADR-006). */
    public void join(UserId user, InviteLink presentedLink) {
        requireActive();
        if (!inviteLink.equals(presentedLink)) {
            throw new InviteLinkInvalidException("invite link does not match for session " + id);
        }
        if (members.add(user)) {
            events.add(new JamEvent.UserJoined(id, user));
        }
    }

    /** Appends a track to the queue, idempotently by key (POC-3). */
    public void addTrack(UserId actor, TrackId track, IdempotencyKey idempotencyKey) {
        requireActive();
        requireMember(actor);
        if (processedKeys.contains(idempotencyKey)) {
            return; // already applied — no-op
        }
        processedKeys.add(idempotencyKey);
        queue.add(track);
        events.add(new JamEvent.TrackQueued(id, track, actor));
    }

    /** Reorders the queue to a permutation of the current tracks. */
    public void reorderQueue(UserId actor, List<TrackId> ordered) {
        requireActive();
        requireMember(actor);
        if (ordered.size() != queue.size()
                || !new HashSet<>(ordered).containsAll(queue)) {
            throw new IllegalArgumentException("reorder must be a permutation of the queue");
        }
        queue.clear();
        queue.addAll(ordered);
        events.add(new JamEvent.QueueReordered(id, List.copyOf(queue), actor));
    }

    /**
     * Casts a skip vote (ADR-004): the host skips directly; a member adds a
     * vote and the track is skipped once votes reach {@code ceil(members/2)}.
     */
    public void castSkipVote(UserId actor, TrackId track) {
        requireActive();
        requireMember(actor);
        if (actor.equals(host)) {
            advanceQueue(track);
            return;
        }
        skipVotes.computeIfAbsent(track, t -> new HashSet<>()).add(actor);
        events.add(new JamEvent.VoteSkipped(id, track, actor));
        if (skipVotes.get(track).size() >= skipThreshold()) {
            advanceQueue(track);
        }
    }

    /** Transfers host ownership to another member (host-only). */
    public void transferHost(UserId actor, UserId newHost) {
        requireActive();
        requireHost(actor);
        if (!members.contains(newHost)) {
            throw new IllegalArgumentException("new host must be a member");
        }
        UserId previous = host;
        host = newHost;
        events.add(new JamEvent.HostTransferred(id, previous, newHost));
    }

    /** Ends the session (host-only). */
    public void end(UserId actor) {
        requireActive();
        requireHost(actor);
        status = SessionStatus.ENDED;
        events.add(new JamEvent.SessionEnded(id, actor));
    }

    private void advanceQueue(TrackId skippedTrack) {
        if (!queue.isEmpty()) {
            queue.remove(0);
        }
        skipVotes.remove(skippedTrack);
        events.add(new JamEvent.Skipped(id, skippedTrack));
    }

    private int skipThreshold() {
        // ceil(members / 2)
        return (members.size() + 1) / 2;
    }

    private void requireActive() {
        if (status != SessionStatus.ACTIVE) {
            throw new SessionNotActiveException("session " + id + " is not active");
        }
    }

    private void requireMember(UserId user) {
        if (!members.contains(user)) {
            throw new NotMemberException("user " + user + " is not a member of session " + id);
        }
    }

    private void requireHost(UserId user) {
        if (!user.equals(host)) {
            throw new NotHostException("user " + user + " is not the host of session " + id);
        }
    }

    public JamId id() {
        return id;
    }

    public UserId host() {
        return host;
    }

    public Set<UserId> members() {
        return Set.copyOf(members);
    }

    /** The queue, where index 0 is the current track. */
    public List<TrackId> queue() {
        return List.copyOf(queue);
    }

    public TrackId currentTrack() {
        return queue.isEmpty() ? null : queue.get(0);
    }

    public SessionStatus status() {
        return status;
    }

    public InviteLink inviteLink() {
        return inviteLink;
    }

    /** The idempotency keys already processed for this session (POC-3). */
    public Set<IdempotencyKey> processedKeys() {
        return Set.copyOf(processedKeys);
    }

    /** The current skip votes, keyed by track (ADR-004). */
    public Map<TrackId, Set<UserId>> skipVotes() {
        Map<TrackId, Set<UserId>> copy = new HashMap<>();
        skipVotes.forEach((track, voters) -> copy.put(track, Set.copyOf(voters)));
        return copy;
    }

    /** Drains and clears the accumulated domain events. */
    public List<JamEvent> drainEvents() {
        List<JamEvent> drained = List.copyOf(events);
        events.clear();
        return drained;
    }
}
