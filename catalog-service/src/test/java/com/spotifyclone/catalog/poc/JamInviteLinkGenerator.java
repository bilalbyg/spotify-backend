package com.spotifyclone.catalog.poc;

/**
 * POC fixture — the seam for POC-9 (invite link generation).
 *
 * <p>Invariant: generated invite links are unguessable — UUID v4's 128-bit
 * entropy makes collisions and brute-force enumeration infeasible (ADR-006).
 *
 * <p>NOT production code.
 */
public interface JamInviteLinkGenerator {

    /** Returns a fresh, unguessable invite link. */
    String generate();
}
