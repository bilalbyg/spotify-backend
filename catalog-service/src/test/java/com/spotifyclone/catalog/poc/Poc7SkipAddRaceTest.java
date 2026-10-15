package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * POC-7 — skip/add atomicity.
 *
 * <p>Invariant under test: {@code currentTrackId} always equals the queue head.
 * A skip is a two-step operation (remove head + advance current) and must be
 * atomic, so no observer ever sees a torn state.
 *
 * <p><b>Expected red</b> against {@link JamPlaybackService} (Slice 1, two
 * transactions): while skip is paused between its two steps, an observer reads
 * a torn state (queue advanced but current not). Slice 2 makes skip atomic and
 * turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc7SkipAddRaceTest {

    @Autowired
    private JamPlayback playback;

    @Autowired
    private JamPlaybackService playbackService;

    @Test
    void skip_IsAtomic_NoObserverSeesTornState() throws Exception {
        UUID trackA = UUID.randomUUID();
        UUID trackB = UUID.randomUUID();
        UUID sessionId = playback.createSession(trackA, List.of(trackB));

        CountDownLatch stepDone = new CountDownLatch(1);
        CountDownLatch proceed = new CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            // Pause skip between its two steps so the observer can read.
            playbackService.setBetweenStepsHook(() -> {
                stepDone.countDown();
                try {
                    proceed.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });

            Future<?> skipFuture = pool.submit(() -> {
                playback.skip(sessionId);
                return null;
            });

            stepDone.await(30, TimeUnit.SECONDS);

            // Observer reads while skip is paused between its two steps.
            JamPlayback.State torn = playback.observe(sessionId);

            proceed.countDown();
            skipFuture.get(30, TimeUnit.SECONDS);

            // Invariant: current == queue head. In the naive (two-tx) impl the
            // observer sees (current=A, queue=[B]) — torn.
            if (torn.queue().isEmpty()) {
                assertNull(torn.currentTrackId(), "empty queue => no current track");
            } else {
                assertEquals(torn.queue().get(0), torn.currentTrackId(),
                        "currentTrackId must equal queue head (no torn state)");
            }

            // Final state after skip: current advances to B, queue now [B].
            JamPlayback.State after = playback.observe(sessionId);
            assertEquals(trackB, after.currentTrackId());
            assertEquals(List.of(trackB), after.queue());
        } finally {
            playbackService.setBetweenStepsHook(() -> {
            });
            proceed.countDown();
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
