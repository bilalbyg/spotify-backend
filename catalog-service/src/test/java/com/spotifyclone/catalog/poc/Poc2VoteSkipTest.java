package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POC-2 (vote skip) — ADR-004 hybrid skip: host skips directly, others vote;
 * threshold = ceil(activeMemberCount / 2); a member votes at most once.
 *
 * <p>Three invariants:
 * <ol>
 *   <li>Duplicate vote is rejected (UNIQUE constraint).</li>
 *   <li>Below threshold no skip; at/above threshold skip fires.</li>
 *   <li>Concurrent threshold-reaching votes trigger the skip exactly once.</li>
 * </ol>
 *
 * <p>The concurrency heart is invariant 3: the skip claim must be a
 * compare-and-swap so only one voter reports {@code true}. The naive
 * {@link JamSkipVoteService} reports {@code true} for every concurrent
 * threshold-reaching voter (skip fires multiple times); Slice 2 adds a
 * {@code @Version} CAS and reports exactly one {@code true}.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc2VoteSkipTest {

    private static final int MEMBER_COUNT = 5; // host + 4 members; threshold ceil(5/2)=3
    private static final int TRIALS = 200;

    @Autowired
    private JamSessionControl sessionControl;

    @Autowired
    private JamSkipVote skipVote;

    @Test
    void duplicateVote_IsRejected() {
        UUID host = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        UUID trackId = UUID.randomUUID();
        UUID sessionId = sessionControl.createSession(host, MEMBER_COUNT);

        boolean first = skipVote.castSkipVote(sessionId, trackId, member);
        boolean second = skipVote.castSkipVote(sessionId, trackId, member);

        assertFalse(first, "single vote below threshold must not skip");
        assertFalse(second, "duplicate vote must be rejected");
        assertEquals(1, skipVote.voteCount(sessionId, trackId), "duplicate must not double count");
    }

    @Test
    void twoVotes_BelowThreshold_DoNotSkip() {
        UUID host = UUID.randomUUID();
        UUID trackId = UUID.randomUUID();
        UUID sessionId = sessionControl.createSession(host, MEMBER_COUNT);

        boolean v1 = skipVote.castSkipVote(sessionId, trackId, UUID.randomUUID());
        boolean v2 = skipVote.castSkipVote(sessionId, trackId, UUID.randomUUID());

        assertFalse(v1);
        assertFalse(v2);
        assertEquals(2, skipVote.voteCount(sessionId, trackId));
        assertFalse(skipVote.isSkipped(sessionId, trackId), "2 votes < threshold 3 must not skip");
    }

    @Test
    void host_SkipsDirectlyWithoutVoting() {
        UUID host = UUID.randomUUID();
        UUID trackId = UUID.randomUUID();
        UUID sessionId = sessionControl.createSession(host, MEMBER_COUNT);

        boolean skipped = skipVote.castSkipVote(sessionId, trackId, host);

        assertTrue(skipped, "host must skip directly (ADR-004)");
        assertTrue(skipVote.isSkipped(sessionId, trackId));
        assertEquals(0, skipVote.voteCount(sessionId, trackId), "host skip must not create a vote");
    }

    @Test
    void concurrentVotesAtThreshold_TriggerExactlyOneSkip() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(MEMBER_COUNT - 1);
        int violations = 0;

        try {
            for (int trial = 0; trial < TRIALS; trial++) {
                UUID host = UUID.randomUUID();
                UUID trackId = UUID.randomUUID();
                UUID sessionId = sessionControl.createSession(host, MEMBER_COUNT);

                // threshold = 3, so 3 distinct members vote concurrently.
                List<UUID> voters = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
                CyclicBarrier barrier = new CyclicBarrier(voters.size());

                List<Future<Boolean>> futures = new ArrayList<>();
                for (UUID voter : voters) {
                    Callable<Boolean> task = () -> {
                        barrier.await();
                        return skipVote.castSkipVote(sessionId, trackId, voter);
                    };
                    futures.add(pool.submit(task));
                }

                long triggered = 0;
                for (Future<Boolean> future : futures) {
                    if (future.get(30, TimeUnit.SECONDS)) {
                        triggered++;
                    }
                }

                if (triggered != 1) {
                    violations++;
                }
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }

        assertEquals(0, violations,
                "skip fired %d wrong times across %d trials (must be exactly once per trial)"
                        .formatted(violations, TRIALS));
    }
}
