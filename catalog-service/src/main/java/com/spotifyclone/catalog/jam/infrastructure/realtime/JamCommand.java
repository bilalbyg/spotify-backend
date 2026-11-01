package com.spotifyclone.catalog.jam.infrastructure.realtime;

import java.util.List;
import java.util.UUID;

/**
 * A STOMP command payload received on {@code /app/session/{id}/command}.
 * Raw UUIDs (JSON-friendly) rather than domain value objects; the controller
 * wraps them and dispatches to the matching use case. Carries no actor field:
 * the actor identity comes exclusively from the handshake-authenticated STOMP
 * session ({@link JamHandshakeInterceptor}), never from the payload.
 */
public record JamCommand(
        String type,
        UUID track,
        List<UUID> order,
        UUID newHost,
        UUID idempotencyKey) {

    public static final String ADD_TRACK = "add-track";
    public static final String REORDER = "reorder";
    public static final String SKIP = "skip";
    public static final String TRANSFER_HOST = "transfer-host";
    public static final String END = "end";
}
