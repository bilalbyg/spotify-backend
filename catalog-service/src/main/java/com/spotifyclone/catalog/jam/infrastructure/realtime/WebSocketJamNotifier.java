package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.jam.application.port.JamNotifier;
import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket/STOMP adapter for the {@link JamNotifier} port. Broadcasts each
 * event to the session's topic ({@code /topic/session/{id}}), which subscribed
 * clients receive (POC-5).
 */
@Component
public class WebSocketJamNotifier implements JamNotifier {

    private final SimpMessagingTemplate template;

    public WebSocketJamNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void notify(JamEvent event) {
        String destination = "/topic/session/" + event.sessionId().value();
        template.convertAndSend(destination, event);
    }

    @Override
    public void notifyRaw(JamId sessionId, String payload) {
        String destination = "/topic/session/" + sessionId.value();
        template.convertAndSend(destination, payload);
    }
}
