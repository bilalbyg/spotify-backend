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
 * POC-3 — idempotency key replay.
 *
 * <p>Invariant under test: a command keyed by {@code (sessionId,
 * idempotencyKey)} is applied at most once. The same key retried sequentially
 * or concurrently must not append a second track.
 *
 * <p><b>Expected red</b> against {@link JamIdempotentQueueService} (Slice 1,
 * key ignored): retrying the same key appends a duplicate track, so the queue
 * has two entries. Slice 2 adds the dedup table and turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc3IdempotencyKeyTest {

    @Autowired
    private JamIdempotentQueue idemQueue;

    @Test
    void sameIdempotencyKey_IsAppliedOnlyOnce_Sequential() {
        UUID sessionId = idemQueue.createSession();
        UUID trackId = UUID.randomUUID();
        UUID key = UUID.randomUUID();

        JamIdempotentQueue.AppendResult first = idemQueue.append(sessionId, trackId, key);
        JamIdempotentQueue.AppendResult retry = idemQueue.append(sessionId, trackId, key);

        assertTrue(first.applied(), "first append must be applied");
        assertFalse(retry.applied(), "retry with the same key must be a no-op");
        assertEquals(1, idemQueue.queue(sessionId).size(),
                "retry must not add a duplicate track");

        // A different key still appends.
        JamIdempotentQueue.AppendResult third =
                idemQueue.append(sessionId, UUID.randomUUID(), UUID.randomUUID());
        assertTrue(third.applied());
        assertEquals(2, idemQueue.queue(sessionId).size());
    }

    @Test
    void sameIdempotencyKey_Concurrent_AppliedExactlyOnce() throws Exception {
        int trials = 200;
        int violations = 0;

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            for (int trial = 0; trial < trials; trial++) {
                UUID sessionId = idemQueue.createSession();
                UUID trackId = UUID.randomUUID();
                UUID key = UUID.randomUUID();

                CyclicBarrier barrier = new CyclicBarrier(2);
                List<Future<JamIdempotentQueue.AppendResult>> futures = new ArrayList<>();

                for (int i = 0; i < 2; i++) {
                    Callable<JamIdempotentQueue.AppendResult> task = () -> {
                        barrier.await();
                        return idemQueue.append(sessionId, trackId, key);
                    };
                    futures.add(pool.submit(task));
                }

                long applied = 0;
                for (Future<JamIdempotentQueue.AppendResult> future : futures) {
                    if (future.get(30, TimeUnit.SECONDS).applied()) {
                        applied++;
                    }
                }

                if (applied != 1 || idemQueue.queue(sessionId).size() != 1) {
                    violations++;
                }
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }

        assertEquals(0, violations,
                "concurrent same-key appends applied wrong number of times in %d trials"
                        .formatted(violations));
    }
}
