package com.spotifyclone.catalog.jam.infrastructure.persistence;

import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA adapter for the {@link JamRepository} port. Loads and saves the aggregate
 * through the Spring Data repository, mapping between the pure domain object
 * and its persistence shape.
 */
@Component
public class JamRepositoryJpaAdapter implements JamRepository {

    private final JamSessionJpaRepository repository;
    private final JamSessionMapper mapper;

    public JamRepositoryJpaAdapter(JamSessionJpaRepository repository, JamSessionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<JamSession> findById(JamId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<JamSession> findByInviteLink(InviteLink inviteLink) {
        return repository.findByInviteLink(inviteLink.value()).map(mapper::toDomain);
    }

    @Override
    public void save(JamSession session) {
        JamSessionJpaEntity entity = mapper.toEntity(session);
        // The pure domain aggregate does not track the optimistic-lock version,
        // so carry it over from the existing row (if any) to let Hibernate's
        // @Version perform the read-modify-write check (POC-1/2).
        repository.findById(session.id().value())
                .ifPresent(existing -> entity.setVersion(existing.getVersion()));
        repository.save(entity);
    }
}
