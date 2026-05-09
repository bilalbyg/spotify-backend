package com.spotifyclone.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

// Bu entity, startup bootstrap islemlerinin tek sefer calistigini isaretlemek icin kullanilir.
@Entity
// Bootstrap marker kayitlarini ayri bir tabloda tutuyoruz.
@Table(name = "bootstrap_state")
// Lombok getter uretir.
@Getter
// Lombok setter uretir.
@Setter
// Lombok no-args constructor uretir.
@NoArgsConstructor
// Lombok all-args constructor uretir.
@AllArgsConstructor
// Lombok builder API'si uretir.
@Builder
public class BootstrapState {

    // Marker kimligini tablo primary key'i olarak tutuyoruz.
    @Id
    // Key alaninin bos olmamasini zorluyoruz.
    @Column(nullable = false, length = 100)
    private String key;

    // Marker kaydinin ne zaman olustugunu izlemek icin timestamp tutuyoruz.
    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
