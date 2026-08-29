package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.TipScrap;
import com.sinchonthon.team3_backend.domain.tip.TipScrapId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipScrapRepository extends JpaRepository<TipScrap, TipScrapId> {
}
