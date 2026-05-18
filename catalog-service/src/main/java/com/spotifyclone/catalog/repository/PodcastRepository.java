package com.spotifyclone.catalog.repository;

import com.spotifyclone.catalog.model.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, UUID>, JpaSpecificationExecutor<Podcast> {
}
