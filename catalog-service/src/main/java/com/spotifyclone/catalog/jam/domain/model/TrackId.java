package com.spotifyclone.catalog.jam.domain.model;

import java.util.UUID;

/** A catalog track identifier. */
public record TrackId(UUID value) {
    public TrackId {
        if (value == null) {
            throw new IllegalArgumentException("track id must not be null");
        }
    }
}
