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

@Entity
@Table(name = "bootstrap_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootstrapState {

    @Id
    // Key alaninin bos olmamasini zorluyoruz.
    @Column(name = "state_key", nullable = false, length = 100)
    private String key;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
