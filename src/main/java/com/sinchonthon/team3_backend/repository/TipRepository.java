package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.Tip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;

public interface TipRepository extends JpaRepository<Tip, Long> {

    @Query("""
            SELECT new com.sinchonthon.team3_backend.dto.response.TipFeedResponse(
                t.id, t.title, c.id, c.name, p.id, p.name,
                u.id, u.nickname, u.trustScore, u.livingAloneYears,
                t.isFiltered, t.createdAt,
                (SELECT COUNT(r) FROM TipReaction r WHERE r.tip = t AND r.isLike = true)
            )
            FROM Tip t
            JOIN t.category c
            JOIN t.place p
            JOIN t.user u
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:userId IS NULL OR u.id = :userId)
              AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR t.content LIKE CONCAT('%', :keyword, '%'))
            ORDER BY t.createdAt DESC
            """)
    Page<TipFeedResponse> findFeedByLatest(@Param("categoryId") Long categoryId, @Param("userId") Long userId,
            @Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT new com.sinchonthon.team3_backend.dto.response.TipFeedResponse(
                t.id, t.title, c.id, c.name, p.id, p.name,
                u.id, u.nickname, u.trustScore, u.livingAloneYears,
                t.isFiltered, t.createdAt,
                (SELECT COUNT(r) FROM TipReaction r WHERE r.tip = t AND r.isLike = true)
            )
            FROM Tip t
            JOIN t.category c
            JOIN t.place p
            JOIN t.user u
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:userId IS NULL OR u.id = :userId)
              AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR t.content LIKE CONCAT('%', :keyword, '%'))
            ORDER BY (SELECT COUNT(r2) FROM TipReaction r2 WHERE r2.tip = t AND r2.isLike = true) DESC
            """)
    Page<TipFeedResponse> findFeedByLikes(@Param("categoryId") Long categoryId, @Param("userId") Long userId,
            @Param("keyword") String keyword, Pageable pageable);
}
