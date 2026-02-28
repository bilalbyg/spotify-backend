package com.spotifyclone.catalog.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongEventProducer {

    // Spring Kafka'nın bize sunduğu sihirli mesaj gönderme aracı
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // Mesajların gideceği "Kanal" (Topic) adı
    private static final String TOPIC = "song-created-topic";

    public void sendSongCreatedEvent(SongCreatedEvent event) {
        log.info("Kafka'ya 'Yeni Şarkı' eventi fırlatılıyor => {}", event);

        // Topic adı, Mesajın Anahtarı (ID), ve Mesajın Kendisi (JSON)
        kafkaTemplate.send(TOPIC, event.id().toString(), event);
    }
}