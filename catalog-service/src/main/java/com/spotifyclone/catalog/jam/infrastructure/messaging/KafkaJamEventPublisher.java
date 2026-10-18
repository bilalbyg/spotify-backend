package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Kafka adapter for the {@link JamEventPublisher} port. Publishes outbox events
 * to the {@code jam-events} topic, blocking on the send so a broker failure
 * surfaces synchronously (the poller then leaves the row unprocessed — POC-4).
 */
@Component
public class KafkaJamEventPublisher implements JamEventPublisher {

    private static final String TOPIC = "jam-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaJamEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String eventType, String payload) {
        try {
            kafkaTemplate.send(TOPIC, eventType, payload).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted while publishing " + eventType, e);
        } catch (Exception e) {
            throw new RuntimeException("kafka publish failed for " + eventType, e);
        }
    }
}
