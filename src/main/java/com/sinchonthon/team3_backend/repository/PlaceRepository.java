package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.place.Place;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("""
            SELECT DISTINCT p
            FROM Tip t
            JOIN t.place p
            JOIN t.category c
            JOIN t.user u
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:userId IS NULL OR u.id = :userId)
              AND (:minLat IS NULL OR p.latitude >= :minLat)
              AND (:maxLat IS NULL OR p.latitude <= :maxLat)
              AND (:minLng IS NULL OR p.longitude >= :minLng)
              AND (:maxLng IS NULL OR p.longitude <= :maxLng)
            """)
    List<Place> findMapPlaces(@Param("categoryId") Long categoryId,
            @Param("userId") Long userId,
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng);
}
