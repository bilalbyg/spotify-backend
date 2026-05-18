package com.spotifyclone.auth.repository;

import com.spotifyclone.auth.model.BootstrapState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Bootstrap marker tablosu icin JPA repository.
public interface BootstrapStateRepository extends JpaRepository<BootstrapState, String>, JpaSpecificationExecutor<BootstrapState> {
}
