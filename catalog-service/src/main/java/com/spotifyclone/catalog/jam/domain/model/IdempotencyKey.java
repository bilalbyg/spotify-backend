package com.spotifyclone.catalog.jam.domain.model;

import java.util.UUID;

/** Client-supplied idempotency key for a command. */
public record IdempotencyKey(UUID value) {
    public IdempotencyKey {
        if (value == null) {
            throw new IllegalArgumentException("idempotency key must not be null");
        }
    }
}
