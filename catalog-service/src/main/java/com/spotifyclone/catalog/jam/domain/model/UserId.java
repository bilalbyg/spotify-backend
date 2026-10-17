package com.spotifyclone.catalog.jam.domain.model;

import java.util.UUID;

/** A user identity (participant or host). */
public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("user id must not be null");
        }
    }
}
