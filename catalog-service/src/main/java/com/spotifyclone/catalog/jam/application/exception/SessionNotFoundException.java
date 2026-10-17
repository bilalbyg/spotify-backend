package com.spotifyclone.catalog.jam.application.exception;

import com.spotifyclone.catalog.jam.domain.model.JamId;

/** Thrown when a use case looks up a session that does not exist. */
public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(JamId id) {
        super("session not found: " + id);
    }
}
