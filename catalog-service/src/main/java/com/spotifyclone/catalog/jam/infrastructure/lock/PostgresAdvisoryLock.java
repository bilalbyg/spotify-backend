package com.spotifyclone.catalog.jam.infrastructure.lock;

import com.spotifyclone.catalog.jam.application.port.JamWorkerLock;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Postgres advisory-lock adapter for the {@link JamWorkerLock} port (ADR-003).
 * {@code pg_try_advisory_lock(hashtext(?))} is a session-scoped, database-level
 * lock: two connections (standing in for two application instances) contend on
 * the same key and only one acquires it. Acquire/release use the same
 * connection, and release happens in a {@code finally} so a work failure cannot
 * leak the lock. Proven in POC-6.
 */
@Component
public class PostgresAdvisoryLock implements JamWorkerLock {

    private final DataSource dataSource;

    public PostgresAdvisoryLock(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean withLock(JamId sessionId, Runnable work) {
        String key = "jam:" + sessionId.value();
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
