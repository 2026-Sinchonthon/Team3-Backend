package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByNicknameAndIdNot(String nickname, Long id);

    @Modifying
    @Query(value = "DELETE FROM tip_comments WHERE user_id = :userId", nativeQuery = true)
    void deleteCommentsByUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM tips WHERE user_id = :userId", nativeQuery = true)
    void deleteTipsByUserId(Long userId);
}
