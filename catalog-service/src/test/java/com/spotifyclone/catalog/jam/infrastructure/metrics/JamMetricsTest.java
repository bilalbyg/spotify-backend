package com.spotifyclone.catalog.jam.infrastructure.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JamMetricsTest {

    @Test
    void counters_areRegisteredAndIncrement() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        JamMetrics metrics = new JamMetrics(registry);

        metrics.sessionCreated();
        metrics.skipVoted();

        assertThat(registry.get("jam_sessions_created_total").counter().count()).isEqualTo(1.0);
        assertThat(registry.get("jam_skip_votes_total").counter().count()).isEqualTo(1.0);
    }
}
