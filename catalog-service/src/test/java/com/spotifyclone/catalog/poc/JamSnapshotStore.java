package com.spotifyclone.catalog.poc;

import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-8 (stale snapshot detection).
 *
 * <p>Invariant: a snapshot is applied only if its version is newer than the
 * currently applied one. A stale (older-version) snapshot arriving late must be
 * discarded, so the stored value always reflects the latest version.
 *
 * <p>NOT production code.
 */
public interface JamSnapshotStore {

    /** Applies the snapshot only if {@code version} is newer than the current. */
    void apply(UUID sessionId, long version, String value);

    /** Returns the current snapshot value. */
    String currentValue(UUID sessionId);

    /** Returns the current snapshot version. */
    long currentVersion(UUID sessionId);
}
