package com.spotifyclone.catalog.jam.domain.exception;

/** Thrown when a join attempt presents an invite link that does not match. */
public class InviteLinkInvalidException extends RuntimeException {
    public InviteLinkInvalidException(String message) {
        super(message);
    }
}
