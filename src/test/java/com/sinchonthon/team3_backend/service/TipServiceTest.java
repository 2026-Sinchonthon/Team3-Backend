package com.sinchonthon.team3_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sinchonthon.team3_backend.domain.place.Place;
import com.sinchonthon.team3_backend.domain.tip.Category;
import com.sinchonthon.team3_backend.domain.tip.Tip;
import com.sinchonthon.team3_backend.domain.tip.TipScrapId;
import com.sinchonthon.team3_backend.domain.user.User;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.TipCommentRepository;
import com.sinchonthon.team3_backend.repository.TipReactionRepository;
import com.sinchonthon.team3_backend.repository.TipRepository;
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
    @Autowired TipRepository tipRepository;
    @Autowired TipScrapRepository tipScrapRepository;
    @Autowired TipReactionRepository tipReactionRepository;
    @Autowired TipCommentRepository tipCommentRepository;
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

        // 반응 11개 중 10개가 비추 -> 최소 표본(11) 이상 + 비추 비율(약 90.9%)이 임계치(30%) 초과로 필터링 대상
        User first = null;
        for (int i = 0; i < 11; i++) {
            User reactor = new User("reactor" + i + "@test.com");
            em.persist(reactor);
            em.flush();
            if (i == 0) first = reactor;
            tipService.react(tip.getId(), reactor.getId(), i == 0);
        }

        var afterElevenReactions = tipService.getDetail(tip.getId(), writer.getId());
        assertThat(afterElevenReactions.likeCount()).isEqualTo(1);
        assertThat(afterElevenReactions.dislikeCount()).isEqualTo(10);
        assertThat(afterElevenReactions.isFiltered()).isTrue();

        // 좋아요 반응 하나를 취소하면 표본 수가 임계치(11) 아래로 떨어져 필터링이 풀린다
        tipService.cancelReaction(tip.getId(), first.getId());

        var afterCancel = tipService.getDetail(tip.getId(), writer.getId());
        assertThat(afterCancel.likeCount()).isEqualTo(0);
        assertThat(afterCancel.dislikeCount()).isEqualTo(10);
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

    @Test
    void 댓글을_작성하고_목록에서_조회하고_본인_댓글만_삭제할_수_있다() {
        User writer = new User("comment-writer@test.com");
        em.persist(writer);
        User other = new User("comment-other@test.com");
        em.persist(other);
        Category category = new Category("공부/작업");
        em.persist(category);
        Place place = new Place("kakao-3", "스터디카페", "서울", BigDecimal.valueOf(37.56), BigDecimal.valueOf(126.94), null);
        em.persist(place);
        Tip tip = new Tip(writer, place, category, "24시간 운영해요", "밤샘 작업 가능", null, Instant.now().plusSeconds(3600));
        em.persist(tip);
        em.flush();

        var comment = tipService.addComment(tip.getId(), other.getId(), "저도 가봤는데 좋아요");
        assertThat(comment.writerId()).isEqualTo(other.getId());
        assertThat(comment.content()).isEqualTo("저도 가봤는데 좋아요");

        var list = tipService.getComments(tip.getId(), org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(list.getTotalElements()).isEqualTo(1);
        assertThat(list.getContent().get(0).commentId()).isEqualTo(comment.commentId());

        // 작성자가 아니면 삭제할 수 없다
        assertThatThrownBy(() -> tipService.deleteComment(comment.commentId(), writer.getId()))
                .isInstanceOf(ApiException.class);

        tipService.deleteComment(comment.commentId(), other.getId());
        var afterDelete = tipService.getComments(tip.getId(), org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(afterDelete.getTotalElements()).isEqualTo(0);
    }

    @Test
    void 본인_게시글만_삭제할_수_있고_삭제하면_반응_스크랩_댓글도_함께_삭제된다() {
        User writer = new User("delete-writer@test.com");
        em.persist(writer);
        User other = new User("delete-other@test.com");
        em.persist(other);
        Category category = new Category("급구 알바");
        em.persist(category);
        Place place = new Place("kakao-4", "편의점", "서울", BigDecimal.valueOf(37.57), BigDecimal.valueOf(126.95), null);
        em.persist(place);
        Tip tip = new Tip(writer, place, category, "급구합니다", "시급 만원", null, Instant.now().plusSeconds(3600));
        em.persist(tip);
        em.flush();

        tipService.react(tip.getId(), other.getId(), true);
        tipService.scrap(tip.getId(), other.getId());
        tipService.addComment(tip.getId(), other.getId(), "지원했어요");

        // 본인 게시글이 아니면 삭제할 수 없다
        assertThatThrownBy(() -> tipService.deleteTip(tip.getId(), other.getId()))
                .isInstanceOf(ApiException.class);

        tipService.deleteTip(tip.getId(), writer.getId());
        em.flush();

        assertThat(tipRepository.existsById(tip.getId())).isFalse();
        assertThat(tipReactionRepository.count()).isEqualTo(0);
        assertThat(tipScrapRepository.count()).isEqualTo(0);
        assertThat(tipCommentRepository.count()).isEqualTo(0);
    }

    @Test
    void 신뢰도순과_관련도순으로_피드를_정렬할_수_있다() {
        User highTrustWriter = new User("high-trust@test.com");
        em.persist(highTrustWriter);
        User lowTrustWriter = new User("low-trust@test.com");
        em.persist(lowTrustWriter);
        em.flush();
        em.createQuery("UPDATE User u SET u.trustScore = 90 WHERE u.id = :id")
                .setParameter("id", highTrustWriter.getId()).executeUpdate();
        em.createQuery("UPDATE User u SET u.trustScore = 10 WHERE u.id = :id")
                .setParameter("id", lowTrustWriter.getId()).executeUpdate();
        em.clear();
        highTrustWriter = em.find(User.class, highTrustWriter.getId());
        lowTrustWriter = em.find(User.class, lowTrustWriter.getId());

        Category category = new Category("생활 꿀팁");
        em.persist(category);
        Place place = new Place("kakao-5", "올리브영", "서울", BigDecimal.valueOf(37.55), BigDecimal.valueOf(126.93), null);
        em.persist(place);
        Tip lowTrustTip = new Tip(lowTrustWriter, place, category, "세일 정보", "세일해요", null, Instant.now().plusSeconds(3600));
        em.persist(lowTrustTip);
        Tip highTrustTip = new Tip(highTrustWriter, place, category, "적립 팁", "적립 잘 챙기세요", null, Instant.now().plusSeconds(3600));
        em.persist(highTrustTip);
        em.flush();

        var byTrust = tipService.getFeed(null, null, null, "trust",
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(byTrust.getContent().get(0).tipId()).isEqualTo(highTrustTip.getId());

        var byRelevance = tipService.getFeed(null, null, "적립", "relevance",
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(byRelevance.getContent()).hasSize(1);
        assertThat(byRelevance.getContent().get(0).tipId()).isEqualTo(highTrustTip.getId());
    }

    @Test
    void 관련도순은_제목과_본문_모두_포함_제목만_포함_본문만_포함_순으로_정렬되고_띄어쓰기_차이는_무시된다() {
        User writer = new User("relevance-writer@test.com");
        em.persist(writer);
        Category category = new Category("식비 방어");
        em.persist(category);
        Place place = new Place("kakao-6", "국밥집", "서울", BigDecimal.valueOf(37.55), BigDecimal.valueOf(126.92), null);
        em.persist(place);

        Tip contentOnly = new Tip(writer, place, category, "저렴한 한 끼", "여기 국밥이 진짜 맛있어요", null,
                Instant.now().plusSeconds(3600));
        em.persist(contentOnly);
        Tip titleOnly = new Tip(writer, place, category, "국밥 맛집 추천", "가성비 좋아요", null,
                Instant.now().plusSeconds(3600));
        em.persist(titleOnly);
        Tip both = new Tip(writer, place, category, "국밥 맛집", "여기 국밥 진짜 맛있어요", null,
                Instant.now().plusSeconds(3600));
        em.persist(both);
        // 띄어쓰기가 다르게 들어간 제목("국 밥")도 "국밥"으로 검색했을 때 매칭되어야 한다
        Tip spaced = new Tip(writer, place, category, "국 밥 신메뉴 나왔어요", "기대되네요", null,
                Instant.now().plusSeconds(3600));
        em.persist(spaced);
        em.flush();

        var result = tipService.getFeed(null, null, "국밥", "relevance",
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent().get(0).tipId()).isEqualTo(both.getId());
        assertThat(result.getContent().get(3).tipId()).isEqualTo(contentOnly.getId());

        // 검색어에 띄어쓰기가 섞여 있어도 동일하게 매칭된다
        var resultWithSpacedKeyword = tipService.getFeed(null, null, "국 밥", "relevance",
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(resultWithSpacedKeyword.getContent()).hasSize(4);
        assertThat(resultWithSpacedKeyword.getContent().get(0).tipId()).isEqualTo(both.getId());
    }
}
