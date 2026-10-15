package com.spotifyclone.catalog.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 1 naive implementation</b> behind the
 * {@link JamPlayback} seam.
 *
 * <p>{@code skip} performs its two steps (remove head + advance current) in a
 * <b>single transaction</b>, so an observer on another connection never sees a
 * torn state — the intermediate is uncommitted and invisible under READ
 * COMMITTED. The test hook still fires, but inside the transaction. Slice 1
 * (red) split the two steps across two transactions and exposed the torn
 * window.
 *
 * <p>NOT production code.
 */
@Service
public class JamPlaybackService implements JamPlayback {

    private static final TypeReference<List<UUID>> UUID_LIST = new TypeReference<>() {
    };

    private final JamPlaybackRepository repository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    private volatile Runnable betweenStepsHook = () -> {
    };

    public JamPlaybackService(
            JamPlaybackRepository repository,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /** Test hook: runs between the two non-atomic skip steps (Slice 1 only). */
    public void setBetweenStepsHook(Runnable betweenStepsHook) {
        this.betweenStepsHook = betweenStepsHook;
    }

    @Override
    public UUID createSession(UUID firstTrack, List<UUID> rest) {
        List<UUID> queue = new ArrayList<>();
        queue.add(firstTrack); // queue[0] is the current track
        queue.addAll(rest);
        return transactionTemplate.execute(status -> {
            JamPlaybackEntity e = new JamPlaybackEntity(
                    UUID.randomUUID(), firstTrack, serialize(queue));
            return repository.save(e).getId();
        });
    }

    @Override
    public void skip(UUID sessionId) {
        // Atomic: both steps in one transaction. An observer using another
        // connection (READ COMMITTED) sees either the fully-old or fully-new
        // state, never the torn intermediate — because the intermediate is
        // uncommitted. The test hook still fires, but inside the transaction.
        transactionTemplate.executeWithoutResult(status -> {
            JamPlaybackEntity e = require(sessionId);
            List<UUID> queue = parse(e.getQueueJson());

            if (!queue.isEmpty()) {
                queue.remove(0);
            }
            e.setQueueJson(serialize(queue));

            betweenStepsHook.run();

            e.setCurrentTrackId(queue.isEmpty() ? null : queue.get(0));
            repository.save(e);
        });
    }

    @Override
    public void add(UUID sessionId, UUID trackId) {
        transactionTemplate.executeWithoutResult(status -> {
            JamPlaybackEntity e = require(sessionId);
            List<UUID> queue = parse(e.getQueueJson());
            queue.add(trackId);
            e.setQueueJson(serialize(queue));
            repository.save(e);
        });
    }

    @Override
    public State observe(UUID sessionId) {
        return transactionTemplate.execute(status -> {
            JamPlaybackEntity e = require(sessionId);
            return new State(e.getCurrentTrackId(), parse(e.getQueueJson()));
        });
    }

    private JamPlaybackEntity require(UUID sessionId) {
        return repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + sessionId));
    }

    private List<UUID> parse(String json) {
        try {
            return objectMapper.readValue(json, UUID_LIST);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("corrupt queue json", e);
        }
    }

    private String serialize(List<UUID> queue) {
        try {
            return objectMapper.writeValueAsString(queue);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialize queue", e);
        }
    }
}
