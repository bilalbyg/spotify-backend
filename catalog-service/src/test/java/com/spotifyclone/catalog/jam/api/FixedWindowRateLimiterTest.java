package com.spotifyclone.catalog.jam.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixedWindowRateLimiterTest {

    private final FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(5, 60_000);

    @Test
    void allowsUpToMaxRequests() {
        for (int i = 0; i < 5; i++) {
            assertThat(limiter.allow("client-1")).isTrue();
        }
    }

    @Test
    void throttlesBeyondMaxRequests() {
        for (int i = 0; i < 5; i++) {
            limiter.allow("client-1");
        }
        assertThat(limiter.allow("client-1")).isFalse();
    }

    @Test
    void limitIsPerKey() {
        for (int i = 0; i < 5; i++) {
            limiter.allow("client-1");
        }
        assertThat(limiter.allow("client-1")).isFalse();
        assertThat(limiter.allow("client-2")).isTrue();
    }
}
