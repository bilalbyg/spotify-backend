package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.spotifyclone.catalog.poc.JamSessionControl.Status.ENDED;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * POC-2 — host transfer / session end race.
 *
 * <p>Invariant under test: once {@link JamSessionControl#end} reports success,
 * the session must stay {@code ENDED}. A concurrent {@code transferHost} must
 * not silently clobber it back to {@code ACTIVE} — that is the lost-update bug
 * on the aggregate.
 *
 * <p>Each trial races {@code end()} against {@code transferHost()} on a fresh
 * session, released together by a {@link CyclicBarrier}. Over many trials the
 * invariant must hold for every successful {@code end()}.
 *
 * <p><b>Expected red</b> against {@link JamSessionControlService} (Slice 1, no
 * {@code @Version}): {@code end()} returns success yet the session ends up
 * {@code ACTIVE} again whenever the transfer writes last. Slice 2 adds
 * {@code @Version} + guard re-check and turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc2HostTransferEndRaceTest {

    private static final int TRIALS = 200;

    @Autowired
    private JamSessionControl control;

    @Test
    void end_IsNotSilentlyUndoneByConcurrentHostTransfer() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        int violations = 0;
        int endSucceeded = 0;

        try {
            for (int trial = 0; trial < TRIALS; trial++) {
                UUID hostId = UUID.randomUUID();
                UUID newHostId = UUID.randomUUID();
                UUID sessionId = control.createSession(hostId);

                CyclicBarrier barrier = new CyclicBarrier(2);

                Future<?> endFuture = pool.submit(() -> {
                    barrier.await();
                    control.end(sessionId);
                    return null;
                });
                Future<?> transferFuture = pool.submit(() -> {
                    barrier.await();
                    control.transferHost(sessionId, newHostId);
                    return null;
                });

                boolean endOk = succeeded(endFuture);
                succeeded(transferFuture); // ensure both finished before reading state

                JamSessionControl.SessionState state = control.state(sessionId);

                if (endOk) {
                    endSucceeded++;
                    if (state.status() != ENDED) {
                        violations++;
                    }
                }
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }

        assertEquals(0, violations,
                "end() was silently undone in %d of %d successful ends"
                        .formatted(violations, endSucceeded));
    }

    /** Returns true if the task completed without throwing; false if its guard rejected it. */
    private boolean succeeded(Future<?> future) {
        try {
            future.get(30, TimeUnit.SECONDS);
            return true;
        } catch (ExecutionException e) {
            return false; // task threw (e.g. "session not active")
        } catch (Exception e) {
            throw new RuntimeException("task failed unexpectedly", e);
        }
    }
}
