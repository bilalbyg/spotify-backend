package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * POC-8 — stale snapshot detection.
 *
 * <p>Invariant under test: a snapshot is applied only if its version is newer
 * than the current one. A stale (older-version) snapshot arriving late must be
 * discarded.
 *
 * <p><b>Expected red</b> against {@link JamSnapshotService} (Slice 1, blind
 * overwrite): a stale v4 arriving after v5 clobbers the newer value. Slice 2
 * compares versions and turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc8StaleSnapshotTest {

    @Autowired
    private JamSnapshotStore snapshotStore;

    @Test
    void staleSnapshot_IsDiscarded() {
        UUID sessionId = UUID.randomUUID();

        snapshotStore.apply(sessionId, 5, "v5-value");
        snapshotStore.apply(sessionId, 4, "stale-v4"); // arrives late, must be ignored

        assertEquals("v5-value", snapshotStore.currentValue(sessionId),
                "a stale snapshot must not clobber the newer one");
        assertEquals(5, snapshotStore.currentVersion(sessionId));
    }

    @Test
    void newerSnapshot_Wins() {
        UUID sessionId = UUID.randomUUID();

        snapshotStore.apply(sessionId, 5, "v5-value");
        snapshotStore.apply(sessionId, 6, "v6-value");

        assertEquals("v6-value", snapshotStore.currentValue(sessionId));
        assertEquals(6, snapshotStore.currentVersion(sessionId));
    }
}
