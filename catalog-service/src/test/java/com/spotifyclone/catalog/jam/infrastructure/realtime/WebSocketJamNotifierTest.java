package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketJamNotifierTest {

    private final SimpMessagingTemplate template = mock(SimpMessagingTemplate.class);
    private final WebSocketJamNotifier notifier = new WebSocketJamNotifier(template);

    @Test
    void notify_broadcastsToSessionTopic() {
        JamId sessionId = new JamId(UUID.randomUUID());
        JamEvent event = new JamEvent.TrackQueued(
                sessionId, new TrackId(UUID.randomUUID()), new UserId(UUID.randomUUID()));

        notifier.notify(event);

        verify(template).convertAndSend("/topic/session/" + sessionId.value(), event);
    }
}
