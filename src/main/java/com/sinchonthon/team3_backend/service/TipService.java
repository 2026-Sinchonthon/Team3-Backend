package com.sinchonthon.team3_backend.service;

import com.sinchonthon.team3_backend.domain.Category;
import com.sinchonthon.team3_backend.domain.Place;
import com.sinchonthon.team3_backend.domain.Tip;
import com.sinchonthon.team3_backend.domain.User;
import com.sinchonthon.team3_backend.dto.request.CreateTipRequest;
import com.sinchonthon.team3_backend.dto.response.CreateTipResponse;
import com.sinchonthon.team3_backend.dto.response.MyTipListResponse;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.CategoryRepository;
import com.sinchonthon.team3_backend.repository.PlaceRepository;
import com.sinchonthon.team3_backend.repository.TipRepository;
import com.sinchonthon.team3_backend.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipService {
    private static final long VALID_DAYS = 365;

    private final UserRepository users;
    private final CategoryRepository categories;
    private final PlaceRepository places;
    private final TipRepository tips;

    public TipService(UserRepository users, CategoryRepository categories,
            PlaceRepository places, TipRepository tips) {
        this.users = users;
        this.categories = categories;
        this.places = places;
        this.tips = tips;
    }

    @Transactional
    public CreateTipResponse create(Long userId, CreateTipRequest request) {
        User user = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."));

        String title = request.title().trim();
        String content = request.content().trim();
        CreateTipRequest.Location location = request.location();
        Place place = places.save(new Place(
                location.name().trim(), trimToNull(location.address()),
                location.latitude(), location.longitude()));

        Tip tip = tips.save(new Tip(user, place, category, title, content,
                Instant.now().plus(VALID_DAYS, ChronoUnit.DAYS)));

        return new CreateTipResponse(
                tip.getId(), user.getId(),
                new CreateTipResponse.CategoryInfo(category.getId(), category.getName()),
                tip.getTitle(), tip.getContent(),
                new CreateTipResponse.PlaceInfo(
                        place.getId(), place.getName(), place.getAddress(),
                        place.getLatitude(), place.getLongitude()),
                tip.getValidUntil(), tip.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public MyTipListResponse findMine(Long userId, int page, int size) {
        if (page < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다.");
        }
        if (size < 1 || size > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상 100 이하여야 합니다.");
        }
        if (!users.existsById(userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        Page<Tip> result = tips.findAllByUserId(
                userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Instant now = Instant.now();
        return new MyTipListResponse(
                result.getContent().stream().map(tip -> toMyTipItem(tip, now)).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages(), result.hasNext());
    }

    private MyTipListResponse.TipItem toMyTipItem(Tip tip, Instant now) {
        Category category = tip.getCategory();
        Place place = tip.getPlace();
        MyTipListResponse.Status status = tip.getValidUntil().isAfter(now)
                ? MyTipListResponse.Status.ACTIVE
                : MyTipListResponse.Status.EXPIRED;
        return new MyTipListResponse.TipItem(
                tip.getId(),
                new MyTipListResponse.CategoryInfo(category.getId(), category.getName()),
                tip.getTitle(), tip.getContent(),
                new MyTipListResponse.PlaceInfo(
                        place.getId(), place.getName(), place.getAddress(),
                        place.getLatitude(), place.getLongitude()),
                status, tip.getValidUntil(), tip.getCreatedAt(), tip.getUpdatedAt());
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
