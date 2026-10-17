package com.spotifyclone.catalog.poc;

/**
 * POC fixture — the seam under test for POC-9 (enumeration rate limiting).
 *
 * <p>Invariant: a client that issues too many join attempts in a window is
 * throttled ({@code false} → 429), while normal clients remain allowed. This
 * blocks enumeration of unguessable invite links.
 *
 * <p>NOT production code.
 */
public interface JamJoinRateLimiter {

    /** Returns {@code true} if the request is allowed, {@code false} if throttled. */
    boolean allow(String clientKey);
}
