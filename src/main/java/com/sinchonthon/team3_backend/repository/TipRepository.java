package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.Tip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface TipRepository extends JpaRepository<Tip, Long> {
    @EntityGraph(attributePaths = {"category", "place"})
    Page<Tip> findAllByUserId(Long userId, Pageable pageable);
}
