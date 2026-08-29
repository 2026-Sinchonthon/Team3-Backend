package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.RefreshSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, String> {
    Optional<RefreshSession> findByTokenHash(String tokenHash);
}
