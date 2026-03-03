package com.spotifyclone.notification.consumer;

import com.spotifyclone.notification.event.SongCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j // Konsola şık loglar basmak için
public class NotificationConsumer {

    // "song-created-topic" kanalını dinle, mesaj gelince bu metodu çalıştır!
    @KafkaListener(topics = "song-created-topic", groupId = "notification-group")
    public void consumeSongCreatedEvent(SongCreatedEvent event) {

        log.info("=====================================================");
        log.info("🔔 BİLDİRİM GÖNDERİLİYOR!");
        log.info("🎵 Yeni Şarkı: {} - {}", event.artist(), event.title());
        log.info("Kullanıcılara push notification atıldı varsayalım...");
        log.info("=====================================================");

    }
}