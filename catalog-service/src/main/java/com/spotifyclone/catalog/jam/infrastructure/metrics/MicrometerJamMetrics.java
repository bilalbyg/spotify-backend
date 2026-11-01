package com.spotifyclone.catalog.jam.infrastructure.metrics;

import com.spotifyclone.catalog.jam.application.port.JamMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Micrometer adapter for the {@link JamMetrics} port (Phase 8). Counters are
 * incremented by the use cases and scraped from {@code /actuator/prometheus}.
 */
@Component
public class MicrometerJamMetrics implements JamMetrics {

    private final Counter sessionsCreated;
    private final Counter skipVotes;

    public MicrometerJamMetrics(MeterRegistry registry) {
        this.sessionsCreated = Counter.builder("jam_sessions_created_total")
                .description("Total jam sessions created")
                .register(registry);
        this.skipVotes = Counter.builder("jam_skip_votes_total")
                .description("Total skip votes cast")
                .register(registry);
    }

    @Override
    public void sessionCreated() {
        sessionsCreated.increment();
    }

    @Override
    public void skipVoted() {
        skipVotes.increment();
    }
}
