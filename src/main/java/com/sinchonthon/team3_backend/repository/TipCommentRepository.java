package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.TipComment;
import com.sinchonthon.team3_backend.dto.response.TipCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipCommentRepository extends JpaRepository<TipComment, Long> {

    @Query("""
            SELECT new com.sinchonthon.team3_backend.dto.response.TipCommentResponse(
                c.id, c.tip.id, u.id, u.nickname, u.trustScore, u.livingAloneYears,
                c.content, c.createdAt, c.updatedAt
            )
            FROM TipComment c
            JOIN c.user u
            WHERE c.tip.id = :tipId
            ORDER BY c.createdAt ASC
            """)
    Page<TipCommentResponse> findByTipId(@Param("tipId") Long tipId, Pageable pageable);
}
