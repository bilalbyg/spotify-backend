package com.spotifyclone.catalog.jam.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fixed-window rate limiter (POC-9 pattern). Throttles a client key after it
 * exceeds {@code maxRequests} within a window. Protects the jam join endpoint
 * against invite-link enumeration (ADR-006). Per-window state is guarded by its
 * own monitor, so concurrent requests for the same key serialize.
 */
@Component
public class FixedWindowRateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(
            @Value("${jam.rate-limit.max:100}") int maxRequests,
            @Value("${jam.rate-limit.window-ms:60000}") long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    /** Returns true if the request is allowed, false if the key is throttled. */
    public boolean allow(String key) {
        long now = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(key, k -> new Window(now, 0));
        synchronized (window) {
            if (now - window.start >= windowMillis) {
                window.start = now;
                window.count = 0;
            }
            if (window.count >= maxRequests) {
                return false;
            }
            window.count++;
            return true;
        }
    }

    private static final class Window {
        long start;
        int count;

        Window(long start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}
