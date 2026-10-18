package com.spotifyclone.catalog.jam.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Jam domain metrics (Phase 8). Counters are incremented by the use cases and
 * scraped from {@code /actuator/prometheus}.
 */
@Component
public class JamMetrics {

    private final Counter sessionsCreated;
    private final Counter skipVotes;

    public JamMetrics(MeterRegistry registry) {
        this.sessionsCreated = Counter.builder("jam_sessions_created_total")
                .description("Total jam sessions created")
                .register(registry);
        this.skipVotes = Counter.builder("jam_skip_votes_total")
                .description("Total skip votes cast")
                .register(registry);
    }

    public void sessionCreated() {
        sessionsCreated.increment();
    }

    public void skipVoted() {
        skipVotes.increment();
    }
}
