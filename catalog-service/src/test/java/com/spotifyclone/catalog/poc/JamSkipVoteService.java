package com.spotifyclone.catalog.poc;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 compare-and-swap vote-skip implementation</b>
 * behind the {@link JamSkipVote} seam.
 *
 * <p>Dedup (via {@link JamVoteRepository}) and threshold are unchanged; the
 * <b>skip claim is now a CAS</b>. When several votes reach the threshold
 * concurrently, each tries to write {@code skippedTrackId}; {@code @Version}
 * lets exactly one win, the losers catch
 * {@link ObjectOptimisticLockingFailureException}, reload, and see the marker
 * already set — returning {@code false} instead of throwing. Net effect: the
 * skip fires exactly once.
 *
 * <p>Slice 1 (red) had a naive claim that set the marker with no retry, so
 * concurrent threshold-reaching votes surfaced an unhandled optimistic-lock
 * exception. NOT production code.
 */
@Service
public class JamSkipVoteService implements JamSkipVote {

    private static final int MAX_RETRIES = 25;

    private final JamSessionControlRepository sessionRepository;
    private final JamVoteRepository voteRepository;
    private final TransactionTemplate transactionTemplate;

    public JamSkipVoteService(
            JamSessionControlRepository sessionRepository,
            JamVoteRepository voteRepository,
            PlatformTransactionManager transactionManager) {
        this.sessionRepository = sessionRepository;
        this.voteRepository = voteRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public boolean castSkipVote(UUID sessionId, UUID trackId, UUID userId) {
        JamSessionControlEntity session = load(sessionId);

        // ADR-004: host bypasses voting and skips directly.
        if (session.getHostUserId().equals(userId)) {
            return claimSkip(sessionId, trackId);
        }

        // Member vote, dedup via the UNIQUE (session, track, user) key.
        try {
            voteRepository.save(new JamVoteEntity(sessionId, trackId, userId));
        } catch (DataIntegrityViolationException e) {
            return false; // already voted on this track
        }

        long votes = voteRepository.countBySessionIdAndTrackId(sessionId, trackId);
        if (votes >= threshold(session)) {
            return claimSkip(sessionId, trackId);
        }
        return false;
    }

    @Override
    public long voteCount(UUID sessionId, UUID trackId) {
        return transactionTemplate.execute(status ->
                voteRepository.countBySessionIdAndTrackId(sessionId, trackId));
    }

    @Override
    public boolean isSkipped(UUID sessionId, UUID trackId) {
        return transactionTemplate.execute(status ->
                trackId.equals(load(sessionId).getSkippedTrackId()));
    }

    private boolean claimSkip(UUID sessionId, UUID trackId) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> {
                    JamSessionControlEntity session = load(sessionId);
                    // CAS: only the first claimant marks this track as skipped.
                    if (trackId.equals(session.getSkippedTrackId())) {
                        return false;
                    }
                    session.setSkippedTrackId(trackId);
                    sessionRepository.save(session); // @Version check fires here
                    return true;
                });
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("unreachable");
    }

    private int threshold(JamSessionControlEntity session) {
        // ceil(activeMemberCount / 2)
        return (session.getActiveMemberCount() + 1) / 2;
    }

    private JamSessionControlEntity load(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + sessionId));
    }
}
