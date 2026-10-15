package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 Postgres advisory-lock implementation</b> behind the
 * {@link JamAdvisoryLock} seam.
 *
 * <p>{@code pg_try_advisory_lock(hashtext(?))} is a session-scoped, database-level
 * lock: two connections (standing in for two application instances) contend on
 * the same key, and only one acquires it — the other gets {@code false}. This
 * is the distributed mutual exclusion that a JVM-local {@code synchronized} /
 * {@code ReentrantLock} cannot provide across instances.
 *
 * <p>The lock is acquired and released on the same connection, and released in
 * a {@code finally} so a work failure cannot leak it. Slice 1 (red) had no lock
 * and let two workers run the critical section concurrently. NOT production
 * code.
 */
@Service
public class JamAdvisoryLockService implements JamAdvisoryLock {

    private final DataSource dataSource;

    public JamAdvisoryLockService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean withLock(UUID sessionId, Runnable work) {
        String key = "jam:" + sessionId;
        try (Connection connection = dataSource.getConnection()) {
            if (!tryAcquire(connection, key)) {
                return false;
            }
            try {
                work.run();
            } finally {
                release(connection, key);
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("advisory lock failed for " + sessionId, e);
        }
    }

    private boolean tryAcquire(Connection connection, String key) throws SQLException {
        try (PreparedStatement ps =
                     connection.prepareStatement("SELECT pg_try_advisory_lock(hashtext(?))")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }

    private void release(Connection connection, String key) throws SQLException {
        try (PreparedStatement ps =
                     connection.prepareStatement("SELECT pg_advisory_unlock(hashtext(?))")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
            }
        }
    }
}
