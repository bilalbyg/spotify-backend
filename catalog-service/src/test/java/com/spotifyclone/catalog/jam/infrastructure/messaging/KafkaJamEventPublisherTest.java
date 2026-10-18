package com.spotifyclone.catalog.jam.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaJamEventPublisherTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
    private final KafkaJamEventPublisher publisher = new KafkaJamEventPublisher(template);

    @Test
    void publish_sendsToJamEventsTopic() {
        when(template.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        publisher.publish("TrackQueued", "{\"sessionId\":\"x\"}");

        verify(template).send("jam-events", "TrackQueued", "{\"sessionId\":\"x\"}");
    }
}
