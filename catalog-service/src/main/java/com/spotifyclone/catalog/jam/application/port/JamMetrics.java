package com.spotifyclone.catalog.jam.application.port;

/**
 * Outbound port for jam domain metrics. The application layer increments
 * counters through this interface only; the Micrometer adapter lives in the
 * infrastructure layer ({@code infrastructure.metrics.MicrometerJamMetrics}).
 */
public interface JamMetrics {

    void sessionCreated();

    void skipVoted();
}
