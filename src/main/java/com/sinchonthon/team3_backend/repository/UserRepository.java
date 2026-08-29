package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByNicknameAndIdNot(String nickname, Long id);
}
