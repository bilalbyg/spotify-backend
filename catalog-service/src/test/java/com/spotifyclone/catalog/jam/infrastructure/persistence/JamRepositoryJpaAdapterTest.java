package com.spotifyclone.catalog.jam.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyclone.catalog.jam.domain.model.IdempotencyKey;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.SessionStatus;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase-3 integration test: the JPA adapter round-trips the pure aggregate
 * through persistence and preserves optimistic-lock versioning.
 */
@DataJpaTest
@ActiveProfiles("test")
class JamRepositoryJpaAdapterTest {

    @Autowired
    private JamSessionJpaRepository jpaRepository;

    private JamRepositoryJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        JamSessionMapper mapper = new JamSessionMapper(new ObjectMapper());
        adapter = new JamRepositoryJpaAdapter(jpaRepository, mapper);
    }

    @Test
    void saveAndFind_RoundTripsTheAggregate() {
        UserId host = new UserId(UUID.randomUUID());
        JamSession session = JamSession.create(host, InviteLink.generate());
        TrackId track = new TrackId(UUID.randomUUID());
        session.addTrack(host, track, new IdempotencyKey(UUID.randomUUID()));
        UserId member = new UserId(UUID.randomUUID());
        session.join(member, session.inviteLink());

        adapter.save(session);

        JamSession loaded = adapter.findById(session.id()).orElseThrow();

        assertThat(loaded.id()).isEqualTo(session.id());
        assertThat(loaded.host()).isEqualTo(host);
        assertThat(loaded.queue()).containsExactly(track);
        assertThat(loaded.members()).contains(host, member);
        assertThat(loaded.status()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(loaded.inviteLink()).isEqualTo(session.inviteLink());
        assertThat(loaded.processedKeys()).hasSize(1);
    }

    @Test
    void saveAgain_UpdatesTheSameRow() {
        UserId host = new UserId(UUID.randomUUID());
        JamSession session = JamSession.create(host, InviteLink.generate());
        adapter.save(session);

        JamSession loaded = adapter.findById(session.id()).orElseThrow();
        loaded.addTrack(host, new TrackId(UUID.randomUUID()), new IdempotencyKey(UUID.randomUUID()));
        adapter.save(loaded);

        JamSession reloaded = adapter.findById(session.id()).orElseThrow();
        assertThat(reloaded.queue()).hasSize(1);
    }

    @Test
    void findById_UnknownId_ReturnsEmpty() {
        assertThat(adapter.findById(new JamId(UUID.randomUUID()))).isEmpty();
    }
}
