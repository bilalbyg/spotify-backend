package com.spotifyclone.catalog.jam.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Maps between the pure {@link JamSession} aggregate and its JPA persistence
 * shape. Mutable collections are serialized as JSON arrays/maps of UUID
 * strings.
 */
@Component
public class JamSessionMapper {

    private static final TypeReference<List<UUID>> UUID_LIST = new TypeReference<>() {
    };

    private static final TypeReference<Map<UUID, List<UUID>>> VOTES_MAP = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public JamSessionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JamSession toDomain(JamSessionJpaEntity entity) {
        Map<UUID, List<UUID>> rawVotes = readSkipVotes(entity.getSkipVotesJson());
        Map<TrackId, Set<UserId>> skipVotes = new LinkedHashMap<>();
        rawVotes.forEach((track, voters) -> skipVotes.put(
                new TrackId(track),
                voters.stream().map(UserId::new).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))));

        return JamSession.reconstitute(
                new JamId(entity.getId()),
                new UserId(entity.getHostUserId()),
                readUuids(entity.getMembersJson()).stream()
                        .map(UserId::new)
                        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)),
                readUuids(entity.getQueueJson()).stream().map(TrackId::new).toList(),
                entity.getStatus(),
                new InviteLink(entity.getInviteLink()),
                readUuids(entity.getProcessedKeysJson()).stream()
                        .map(IdempotencyKey::new)
                        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)),
                skipVotes);
    }

    public JamSessionJpaEntity toEntity(JamSession session) {
        return new JamSessionJpaEntity(
                session.id().value(),
                session.host().value(),
                session.status(),
                session.inviteLink().value(),
                writeUuids(session.queue().stream().map(TrackId::value).toList()),
                writeUuids(session.members().stream().map(UserId::value).toList()),
                writeUuids(processedKeys(session)),
                writeSkipVotes(session));
    }

    private List<UUID> processedKeys(JamSession session) {
        return session.processedKeys().stream().map(IdempotencyKey::value).toList();
    }

    private String writeSkipVotes(JamSession session) {
        Map<UUID, List<UUID>> map = new LinkedHashMap<>();
        session.skipVotes().forEach((track, voters) ->
                map.put(track.value(), voters.stream().map(UserId::value).toList()));
        return write(map, VOTES_MAP);
    }

    private Map<UUID, List<UUID>> readSkipVotes(String json) {
        return read(json, VOTES_MAP);
    }

    private String writeUuids(List<UUID> uuids) {
        return write(uuids, UUID_LIST);
    }

    private List<UUID> readUuids(String json) {
        return read(json, UUID_LIST);
    }

    private <T> String write(T value, TypeReference<T> type) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialize " + type, e);
        }
    }

    private <T> T read(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot deserialize " + type, e);
        }
    }
}
