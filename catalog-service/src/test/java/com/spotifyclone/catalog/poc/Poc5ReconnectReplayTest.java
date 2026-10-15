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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POC-5 — event log + reconnect replay.
 *
 * <p>Invariant under test: a client that reconnects with its last-seen sequence
 * number replays exactly the events it missed — no lost, no duplicated, no
 * out-of-order events.
 *
 * <p><b>Expected red</b> against {@link JamEventLogService} (Slice 1, inclusive
 * cursor): replay returns the already-processed boundary event too, so a
 * reconnect duplicates one event. Slice 2 corrects the boundary to exclusive.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc5ReconnectReplayTest {

    @Autowired
    private JamEventLog eventLog;

    @Test
    void reconnect_ReplaysExactlyMissedEvents_NoDuplicateNoGap() {
        UUID sessionId = UUID.randomUUID();
        for (int i = 1; i <= 50; i++) {
            eventLog.append(sessionId, "track-added", "t" + i);
        }

        // Client processed seq 1..42, then disconnected. Reconnect and replay
        // from its cursor.
        List<JamEventLog.Event> missed = eventLog.replay(sessionId, 42);

        assertEquals(8, missed.size(),
                "reconnect must replay exactly the 8 missed events (43..50)");
        for (int i = 0; i < missed.size(); i++) {
            assertEquals(43 + i, missed.get(i).seq(),
                    "replayed events must be 43..50, in order, no gap");
        }
    }

    @Test
    void concurrentAppend_AssignsDistinctMonotonicSeq() throws Exception {
        int count = 50;
        UUID sessionId = UUID.randomUUID();
        ExecutorService pool = Executors.newFixedThreadPool(count);
        CyclicBarrier barrier = new CyclicBarrier(count);

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int n = i;
            Callable<Void> task = () -> {
                barrier.await();
                eventLog.append(sessionId, "track-added", "t" + n);
                return null;
            };
            futures.add(pool.submit(task));
        }
        for (Future<?> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        List<JamEventLog.Event> all = eventLog.replay(sessionId, 0);
        assertEquals(count, all.size(), "all events must be retained");
        long distinct = all.stream().map(JamEventLog.Event::seq).distinct().count();
        assertEquals(count, distinct, "sequence numbers must be distinct under concurrency");
    }
}
