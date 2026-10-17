package com.spotifyclone.catalog.jam.domain.exception;

/** Thrown when a user who is not a member of the session attempts an operation. */
public class NotMemberException extends RuntimeException {
    public NotMemberException(String message) {
        super(message);
    }
}
