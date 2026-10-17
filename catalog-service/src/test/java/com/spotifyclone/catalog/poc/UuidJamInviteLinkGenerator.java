package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * POC fixture — UUID v4 invite-link generator (ADR-006). NOT production code.
 */
@Component
public class UuidJamInviteLinkGenerator implements JamInviteLinkGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
