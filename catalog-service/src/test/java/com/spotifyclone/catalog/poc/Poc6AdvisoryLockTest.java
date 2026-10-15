package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POC-6 — distributed mutual exclusion via Postgres advisory lock.
 *
 * <p>Invariant under test: at most one worker at a time runs the critical
 * section for a session. The test holds the lock in worker A while worker B
 * tries to enter; B must be rejected while A holds, and re-acquire only after
 * A releases.
 *
 * <p><b>Expected red</b> against {@link JamAdvisoryLockService} (Slice 1, no
 * lock): worker B also enters the critical section while A is still inside —
 * double-processing. Slice 2 uses {@code pg_try_advisory_lock} and turns this
 * green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc6AdvisoryLockTest {

    @Autowired
    private JamAdvisoryLock advisoryLock;

    @Test
    void onlyOneWorkerHoldsTheLock_OthersAreRejected() throws Exception {
        UUID sessionId = UUID.randomUUID();
        AtomicInteger workRuns = new AtomicInteger();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            // Worker A enters the critical section and holds it.
            Future<Boolean> a = pool.submit(() -> advisoryLock.withLock(sessionId, () -> {
                workRuns.incrementAndGet();
                entered.countDown();
                try {
                    release.await(); // hold the lock until told to release
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("interrupted while holding lock", e);
                }
            }));

            entered.await(30, TimeUnit.SECONDS);

            // Worker B tries to enter while A still holds the lock.
            Future<Boolean> b = pool.submit(() ->
                    advisoryLock.withLock(sessionId, workRuns::incrementAndGet));
            boolean bAcquired = b.get(30, TimeUnit.SECONDS);

            release.countDown(); // let A release

            assertTrue(a.get(30, TimeUnit.SECONDS), "worker A must acquire the lock");
            assertFalse(bAcquired, "worker B must be rejected while A holds the lock");
            assertEquals(1, workRuns.get(), "the critical section must run exactly once");
        } finally {
            release.countDown();
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    @Test
    void lockIsReleased_AfterWorkCompletes() throws Exception {
        UUID sessionId = UUID.randomUUID();
        AtomicInteger workRuns = new AtomicInteger();

        boolean first = advisoryLock.withLock(sessionId, workRuns::incrementAndGet);
        boolean second = advisoryLock.withLock(sessionId, workRuns::incrementAndGet);

        assertTrue(first);
        assertTrue(second, "after release, a new worker must be able to re-acquire");
        assertEquals(2, workRuns.get(), "each acquire runs the work once");
    }
}
