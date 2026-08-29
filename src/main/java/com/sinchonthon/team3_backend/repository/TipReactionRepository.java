package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.Tip;
import com.sinchonthon.team3_backend.domain.tip.TipReaction;
import com.sinchonthon.team3_backend.domain.tip.TipReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipReactionRepository extends JpaRepository<TipReaction, TipReactionId> {
    long countByTip(Tip tip);
    long countByTipAndIsLikeTrue(Tip tip);
    long countByTipAndIsLikeFalse(Tip tip);
}
