package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 implementation</b> behind the
 * {@link JamSnapshotStore} seam.
 *
 * <p>{@code apply} compares the incoming version against the stored one and
 * only overwrites when it is newer. A stale (older-version) snapshot arriving
 * late is discarded. Slice 1 (red) overwrote blindly and let stale clobber
 * newer.
 *
 * <p>NOT production code.
 */
@Service
public class JamSnapshotService implements JamSnapshotStore {

    private final JamSnapshotRepository repository;
    private final TransactionTemplate transactionTemplate;

    public JamSnapshotService(
            JamSnapshotRepository repository,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void apply(UUID sessionId, long version, String value) {
        transactionTemplate.executeWithoutResult(status -> {
            JamSnapshotEntity entity = repository.findById(sessionId)
                    .orElseGet(() -> new JamSnapshotEntity(sessionId, 0, value));
            // Only newer versions win; stale snapshots are discarded.
            if (version > entity.getVersion()) {
                entity.setVersion(version);
                entity.setValue(value);
                repository.save(entity);
            }
        });
    }

    @Override
    public String currentValue(UUID sessionId) {
        return transactionTemplate.execute(status ->
                repository.findById(sessionId).map(JamSnapshotEntity::getValue).orElse(null));
    }

    @Override
    public long currentVersion(UUID sessionId) {
        return transactionTemplate.execute(status ->
                repository.findById(sessionId).map(JamSnapshotEntity::getVersion).orElse(0L));
    }
}
