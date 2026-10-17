package com.spotifyclone.catalog.jam.domain.exception;

/** Thrown when a host-only operation is attempted by a non-host. */
public class NotHostException extends RuntimeException {
    public NotHostException(String message) {
        super(message);
    }
}
