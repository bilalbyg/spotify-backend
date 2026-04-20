package com.spotifyclone.notification.repository;

import com.spotifyclone.notification.model.ListeningHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ListeningHistoryRepository extends MongoRepository<ListeningHistory, String> {
    List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(UUID userId);
}
