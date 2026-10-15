package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POC-1 — queue concurrent modification.
 *
 * <p>Invariant under test: N concurrent {@link JamQueue#append} calls against
 * the same session must all survive — the final queue holds every appended
 * track exactly once, with no lost updates.
 *
 * <p>The test is deliberately NOT transactional: each append runs in its own
 * transaction (its own connection), so the threads genuinely contend on the
 * same row instead of sharing one. A {@link CyclicBarrier} releases all threads
 * at once to maximise contention, replacing a flaky {@code Thread.sleep} with
 * controlled synchronization.
 *
 * <p><b>Expected red</b> against {@link JamQueueService} (Slice 1, no
 * {@code @Version}): the final queue is smaller than 10 — a silent lost update.
 * Slice 2 adds {@code @Version} + retry and turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc1QueueConcurrencyTest {

    private static final int THREAD_COUNT = 10;

    @Autowired
    private JamQueue jamQueue;

    @Test
    void concurrentAppends_doNotLoseUpdates() throws Exception {
        UUID sessionId = jamQueue.createSession();

        List<UUID> tracks = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            tracks.add(UUID.randomUUID());
        }

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
        List<Future<List<UUID>>> futures = new ArrayList<>();

        for (UUID track : tracks) {
            futures.add(pool.submit(() -> {
                barrier.await(); // release all threads together
                return jamQueue.append(sessionId, track);
            }));
        }

        // Wait for every append to finish (or propagate the first failure).
        for (Future<List<UUID>> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        List<UUID> finalQueue = jamQueue.queue(sessionId);

        // Invariant: no append may be lost.
        assertEquals(THREAD_COUNT, finalQueue.size(),
                "lost update: expected %d tracks, got %d".formatted(THREAD_COUNT, finalQueue.size()));
        assertEquals(THREAD_COUNT, new HashSet<>(finalQueue).size(),
                "queue contains duplicate tracks");
    }
}
