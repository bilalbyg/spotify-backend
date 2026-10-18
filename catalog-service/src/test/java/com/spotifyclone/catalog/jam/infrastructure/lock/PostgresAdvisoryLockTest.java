package com.spotifyclone.catalog.jam.infrastructure.lock;

import com.spotifyclone.catalog.jam.application.port.JamWorkerLock;
import com.spotifyclone.catalog.jam.domain.model.JamId;
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
 * Phase-5 integration test: the Postgres advisory lock gives distributed
 * mutual exclusion (ADR-003, POC-6). Requires the compose PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("poc")
class PostgresAdvisoryLockTest {

    @Autowired
    private JamWorkerLock workerLock;

    @Test
    void onlyOneWorkerHoldsTheLock_OthersRejected() throws Exception {
        JamId sessionId = new JamId(UUID.randomUUID());
        AtomicInteger workRuns = new AtomicInteger();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> a = pool.submit(() -> workerLock.withLock(sessionId, () -> {
                workRuns.incrementAndGet();
                entered.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }));

            entered.await(30, TimeUnit.SECONDS);

            Future<Boolean> b = pool.submit(() ->
                    workerLock.withLock(sessionId, workRuns::incrementAndGet));
            boolean bAcquired = b.get(30, TimeUnit.SECONDS);

            release.countDown();

            assertTrue(a.get(30, TimeUnit.SECONDS));
            assertFalse(bAcquired, "worker B must be rejected while A holds the lock");
            assertEquals(1, workRuns.get());
        } finally {
            release.countDown();
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
