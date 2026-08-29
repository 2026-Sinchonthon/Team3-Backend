package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
