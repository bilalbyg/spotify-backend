package com.spotifyclone.catalog;

import com.spotifyclone.catalog.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 0 / context-test baseline fix.
 *
 * <p>The original test relied on a PostgreSQL instance reachable at
 * {@code localhost:5433}, which is environment-dependent and fails in CI and
 * local Linux checkouts that do not have a Postgres service running. Per
 * {@code spotify-backend/AGENTS.md}, context tests must declare their external
 * dependencies explicitly rather than rely on a developer's local services.
 *
 * <p>We instead repoint the datasource to an in-memory H2 instance (PostgreSQL
 * compatibility mode) for the test profile only, configured in
 * {@code src/test/resources/application-test.yml}. Production runtime still
 * points at the real PostgreSQL service as defined in the main
 * {@code application.yml}.
 */
@SpringBootTest
@ActiveProfiles("test")
class CatalogServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Sanity check: the context started successfully and our security
        // configuration is wired up.
        assertThat(applicationContext.getBean(SecurityConfig.class)).isNotNull();
    }
}
