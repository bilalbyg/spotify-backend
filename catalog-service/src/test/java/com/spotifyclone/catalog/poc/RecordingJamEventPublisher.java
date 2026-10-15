package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * POC fixture — an in-memory {@link JamEventPublisher} that records published
 * events and can be told to fail (simulating a broker outage). Registered as a
 * bean so the test can autowire it and flip {@link #setFailMode(boolean)}.
 *
 * <p>NOT production code.
 */
@Component
public class RecordingJamEventPublisher implements JamEventPublisher {

    private final List<JamOutboxEntity> published = new CopyOnWriteArrayList<>();
    private volatile boolean failMode = false;

    @Override
    public void publish(JamOutboxEntity event) {
        if (failMode) {
            throw new RuntimeException("broker down (simulated)");
        }
        published.add(event);
    }

    public void setFailMode(boolean failMode) {
        this.failMode = failMode;
    }

    public int publishedCount() {
        return published.size();
    }

    public List<JamOutboxEntity> published() {
        return List.copyOf(published);
    }

    public void reset() {
        published.clear();
        failMode = false;
    }
}
