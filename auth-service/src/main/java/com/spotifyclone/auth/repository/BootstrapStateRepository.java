package com.spotifyclone.auth.repository;

import com.spotifyclone.auth.model.BootstrapState;
import org.springframework.data.jpa.repository.JpaRepository;

// Bootstrap marker tablosu icin JPA repository.
public interface BootstrapStateRepository extends JpaRepository<BootstrapState, String> {
}
