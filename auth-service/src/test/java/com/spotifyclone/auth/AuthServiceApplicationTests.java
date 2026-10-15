package com.spotifyclone.auth;

import com.spotifyclone.auth.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 0 / context-test baseline fix.
 *
 * <p>The original test relied on a MySQL instance reachable at
 * {@code localhost:3307}; per {@code spotify-backend/AGENTS.md}, context
 * tests must declare external dependencies explicitly. We instead repoint
 * the datasource to an in-memory H2 (MySQL compatibility mode) for the test
 * profile only; production runtime still points at the MySQL service from
 * {@code application.yaml}.
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext.getBean(SecurityConfig.class)).isNotNull();
    }
}
