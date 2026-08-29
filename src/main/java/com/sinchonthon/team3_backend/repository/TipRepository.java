package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.Tip;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sinchonthon.team3_backend.dto.response.TipDetailResponse;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;

public interface TipRepository extends JpaRepository<Tip, Long> {

    @EntityGraph(attributePaths = {"category", "place"})
    Page<Tip> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT new com.sinchonthon.team3_backend.dto.response.TipDetailResponse(
                t.id, t.title, t.content, t.visitedAt, t.validUntil,
                c.id, c.name, p.id, p.name,
                u.id, u.nickname, u.trustScore, u.livingAloneYears,
                t.isFiltered, t.createdAt, t.updatedAt,
                (SELECT COUNT(r) FROM TipReaction r WHERE r.tip = t AND r.isLike = true),
                (SELECT COUNT(r) FROM TipReaction r WHERE r.tip = t AND r.isLike = false),
                (SELECT r.isLike FROM TipReaction r WHERE r.tip = t AND r.user.id = :currentUserId)
            )
            FROM Tip t
            JOIN t.category c
            JOIN t.place p
            JOIN t.user u
            WHERE t.id = :tipId
            """)
    Optional<TipDetailResponse> findDetailById(@Param("tipId") Long tipId, @Param("currentUserId") Long currentUserId);

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
              AND (:keyword IS NULL
                   OR REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')
                   OR REPLACE(t.content, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))
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
              AND (:keyword IS NULL
                   OR REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')
                   OR REPLACE(t.content, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))
            ORDER BY (SELECT COUNT(r2) FROM TipReaction r2 WHERE r2.tip = t AND r2.isLike = true) DESC
            """)
    Page<TipFeedResponse> findFeedByLikes(@Param("categoryId") Long categoryId, @Param("userId") Long userId,
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
              AND (:keyword IS NULL
                   OR REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')
                   OR REPLACE(t.content, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))
            ORDER BY u.trustScore DESC, t.createdAt DESC
            """)
    Page<TipFeedResponse> findFeedByTrust(@Param("categoryId") Long categoryId, @Param("userId") Long userId,
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
              AND (REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')
                   OR REPLACE(t.content, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))
            ORDER BY
                CASE
                    WHEN REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')
                         AND REPLACE(t.content, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') THEN 0
                    WHEN REPLACE(t.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') THEN 1
                    ELSE 2
                END ASC,
                t.createdAt DESC
            """)
    Page<TipFeedResponse> findFeedByRelevance(@Param("categoryId") Long categoryId, @Param("userId") Long userId,
            @Param("keyword") String keyword, Pageable pageable);
}
