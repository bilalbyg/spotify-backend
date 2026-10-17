package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POC-9 — invite link enumeration (ADR-006).
 *
 * <p>Two invariants: UUID v4 links are unique/unguessable over a large sample,
 * and an enumeration attack is rate-limited (throttled after a per-client
 * threshold).
 *
 * <p><b>Expected red</b> against {@link JamJoinRateLimiterService} (Slice 1, no
 * limit): every request is allowed, so the enumeration attack is not throttled.
 * Slice 2 adds a fixed-window limiter and turns it green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc9InviteLinkTest {

    @Autowired
    private JamInviteLinkGenerator linkGenerator;

    @Autowired
    private JamJoinRateLimiter rateLimiter;

    @Test
    void uuidV4Links_AreUniqueOverAMillion() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1_000_000; i++) {
            String link = linkGenerator.generate();
            assertTrue(seen.add(link), "UUID v4 collision at iteration " + i);
        }
        assertEquals(1_000_000, seen.size());
    }

    @Test
    void enumerationAttack_IsRateLimited() {
        String attacker = "client-1";
        // First 5 join attempts are allowed (window size 5).
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allow(attacker), "attempt " + (i + 1) + " should be allowed");
        }
        // The 6th (and onward) is throttled → 429.
        assertFalse(rateLimiter.allow(attacker), "6th attempt must be rate-limited");
    }

    @Test
    void rateLimit_IsPerClient() {
        String attacker = "client-2";
        for (int i = 0; i < 5; i++) {
            rateLimiter.allow(attacker);
        }
        assertFalse(rateLimiter.allow(attacker), "attacker throttled");

        // A different client is unaffected.
        assertTrue(rateLimiter.allow("client-3"), "a different client must not be throttled");
    }
}
