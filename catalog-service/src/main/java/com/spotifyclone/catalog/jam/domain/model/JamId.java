package com.spotifyclone.catalog.jam.domain.model;

import java.util.UUID;

/** Jam session identifier. */
public record JamId(UUID value) {
    public JamId {
        if (value == null) {
            throw new IllegalArgumentException("jam id must not be null");
        }
    }
}
