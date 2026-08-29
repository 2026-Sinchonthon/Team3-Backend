package com.sinchonthon.team3_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sinchonthon.team3_backend.domain.place.Place;
import com.sinchonthon.team3_backend.domain.tip.Category;
import com.sinchonthon.team3_backend.domain.tip.Tip;
import com.sinchonthon.team3_backend.domain.tip.TipScrapId;
import com.sinchonthon.team3_backend.domain.user.User;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.TipScrapRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TipServiceTest {

    @Autowired TipService tipService;
    @Autowired TipScrapRepository tipScrapRepository;
    @Autowired EntityManager em;

    @Test
    void 비추_비율이_임계치를_넘으면_is_filtered가_true가_되고_취소하면_다시_false가_된다() {
        User writer = new User("writer@test.com");
        em.persist(writer);
        Category category = new Category("알바");
        em.persist(category);
        Place place = new Place("kakao-1", "GS25 신촌점", "서울", BigDecimal.valueOf(37.5), BigDecimal.valueOf(127.0), null);
        em.persist(place);
        Tip tip = new Tip(writer, place, category, "시급 괜찮아요", "친절해요", null, Instant.now().plusSeconds(3600));
        em.persist(tip);
        em.flush();

        // 반응 5개 중 4개가 비추 -> 최소 표본(5) 이상 + 비추 비율(80%) 임계치(50%) 초과로 필터링 대상
        User first = null;
        for (int i = 0; i < 5; i++) {
            User reactor = new User("reactor" + i + "@test.com");
            em.persist(reactor);
            em.flush();
            if (i == 0) first = reactor;
            tipService.react(tip.getId(), reactor.getId(), i == 0);
        }

        var afterFiveReactions = tipService.getDetail(tip.getId(), writer.getId());
        assertThat(afterFiveReactions.likeCount()).isEqualTo(1);
        assertThat(afterFiveReactions.dislikeCount()).isEqualTo(4);
        assertThat(afterFiveReactions.isFiltered()).isTrue();

        // 좋아요 반응 하나를 취소하면 표본 수가 임계치 아래로 떨어져 필터링이 풀린다
        tipService.cancelReaction(tip.getId(), first.getId());

        var afterCancel = tipService.getDetail(tip.getId(), writer.getId());
        assertThat(afterCancel.likeCount()).isEqualTo(0);
        assertThat(afterCancel.dislikeCount()).isEqualTo(4);
        assertThat(afterCancel.isFiltered()).isFalse();
    }

    @Test
    void 스크랩을_등록하고_취소할_수_있다() {
        User writer = new User("scrap-writer@test.com");
        em.persist(writer);
        Category category = new Category("생활 꿀팁");
        em.persist(category);
        Place place = new Place("kakao-2", "다이소 신촌점", "서울", BigDecimal.valueOf(37.55), BigDecimal.valueOf(126.93), null);
        em.persist(place);
        Tip tip = new Tip(writer, place, category, "생필품 저렴해요", "1000원샵도 있어요", null, Instant.now().plusSeconds(3600));
        em.persist(tip);
        em.flush();

        tipService.scrap(tip.getId(), writer.getId());
        assertThat(tipScrapRepository.existsById(new TipScrapId(writer.getId(), tip.getId()))).isTrue();

        // 중복 스크랩 요청은 예외 없이 무시된다 (idempotent)
        tipService.scrap(tip.getId(), writer.getId());
        assertThat(tipScrapRepository.count()).isEqualTo(1);

        tipService.cancelScrap(tip.getId(), writer.getId());
        assertThat(tipScrapRepository.existsById(new TipScrapId(writer.getId(), tip.getId()))).isFalse();

        // 스크랩한 적 없는 상태에서 취소하면 예외가 발생한다
        assertThatThrownBy(() -> tipService.cancelScrap(tip.getId(), writer.getId()))
                .isInstanceOf(ApiException.class);
    }
}
