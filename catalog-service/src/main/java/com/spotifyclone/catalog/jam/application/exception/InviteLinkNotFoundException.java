package com.spotifyclone.catalog.jam.application.exception;

import com.spotifyclone.catalog.jam.domain.model.InviteLink;

/** Thrown when no session is found for a presented invite link. */
public class InviteLinkNotFoundException extends RuntimeException {
    public InviteLinkNotFoundException(InviteLink inviteLink) {
        super("no session found for invite link " + inviteLink);
    }
}
