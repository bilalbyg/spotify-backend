package com.spotifyclone.catalog.jam.domain.exception;

/** Thrown when an operation requires an ACTIVE session but it has ended. */
public class SessionNotActiveException extends RuntimeException {
    public SessionNotActiveException(String message) {
        super(message);
    }
}
