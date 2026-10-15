package com.spotifyclone.catalog.poc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 implementation</b> behind the {@link JamEventLog}
 * seam.
 *
 * <p>The replay cursor is exclusive: it replays {@code seq > afterSeq}. A
 * reconnecting client receives exactly the events it missed — no duplicate of
 * the boundary event it already processed, no gap. Slice 1 (red) used an
 * inclusive cursor and duplicated the boundary event.
 *
 * <p>NOT production code.
 */
@Service
public class JamEventLogService implements JamEventLog {

    private final JamEventRepository repository;

    public JamEventLogService(JamEventRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void append(UUID sessionId, String eventType, String payload) {
        repository.save(new JamEventEntity(sessionId, eventType, payload));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> replay(UUID sessionId, long afterSeq) {
        // Exclusive cursor: seq > afterSeq, so the client resumes exactly where
        // it left off.
        return repository.findBySessionIdAndSeqGreaterThanOrderBySeqAsc(sessionId, afterSeq)
                .stream()
                .map(e -> new Event(e.getSeq(), e.getEventType(), e.getPayload()))
                .toList();
    }
}
