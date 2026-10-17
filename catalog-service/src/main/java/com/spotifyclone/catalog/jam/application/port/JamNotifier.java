package com.spotifyclone.catalog.jam.application.port;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;

/**
 * Outbound port for real-time event notification (POC-5). The Phase-4
 * WebSocket/STOMP adapter implements this; the application layer only notifies.
 */
public interface JamNotifier {

    void notify(JamEvent event);
}
