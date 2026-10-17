package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Placeholder {@link JamEventPublisher} so the poller can run before the Kafka
 * adapter lands. Logs instead of publishing. Replaced in the next slice.
 */
@Component
public class LoggingJamEventPublisher implements JamEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingJamEventPublisher.class);

    @Override
    public void publish(String eventType, String payload) {
        log.debug("event publish (placeholder): {} {}", eventType, payload);
    }
}
