package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POC fixture — <b>Slice 2 fixed-window rate limiter</b> behind the
 * {@link JamJoinRateLimiter} seam.
 *
 * <p>Each client key gets its own fixed window; once a client exceeds
 * {@link #MAX_REQUESTS} within {@link #WINDOW_MILLIS}, further requests are
 * throttled ({@code false} → 429). Per-window state is guarded by its own
 * monitor, so concurrent requests from the same client serialize correctly.
 * This blocks enumeration of unguessable UUID v4 invite links.
 *
 * <p>Slice 1 (red) had no limit and allowed everything. NOT production code.
 */
@Service
public class JamJoinRateLimiterService implements JamJoinRateLimiter {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MILLIS = 60_000;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public boolean allow(String clientKey) {
        long now = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(clientKey, k -> new Window(now, 0));
        synchronized (window) {
            if (now - window.start >= WINDOW_MILLIS) {
                window.start = now;
                window.count = 0;
            }
            if (window.count >= MAX_REQUESTS) {
                return false;
            }
            window.count++;
            return true;
        }
    }

    private static class Window {
        long start;
        int count;

        Window(long start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}
