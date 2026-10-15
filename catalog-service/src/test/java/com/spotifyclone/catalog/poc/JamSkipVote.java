package com.spotifyclone.catalog.poc;

import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-2 (vote skip, ADR-004).
 *
 * <p>Invariants POC-2 (vote skip) proves:
 * <ol>
 *   <li><b>Dedup</b>: a member votes at most once per track — the second vote
 *       is rejected, so no double counting.</li>
 *   <li><b>Threshold</b>: the skip is triggered only when the number of
 *       distinct votes reaches {@code ceil(activeMemberCount / 2)}, not
 *       before.</li>
 *   <li><b>Exactly-once skip</b>: when concurrent votes reach the threshold,
 *       the skip fires exactly once — one caller reports {@code true}, the
 *       rest report {@code false}.</li>
 * </ol>
 *
 * <p>The host bypasses voting (ADR-004): {@code castSkipVote} called by the
 * host skips immediately. NOT production code.
 */
public interface JamSkipVote {

    /**
     * Casts a skip vote for {@code trackId}. Returns {@code true} when this
     * call actually triggered the skip (host bypass, or the vote that reached
     * the threshold), {@code false} otherwise.
     */
    boolean castSkipVote(UUID sessionId, UUID trackId, UUID userId);

    /** Returns the number of distinct votes for the given track. */
    long voteCount(UUID sessionId, UUID trackId);

    /** Returns true if the given track was skipped. */
    boolean isSkipped(UUID sessionId, UUID trackId);
}
