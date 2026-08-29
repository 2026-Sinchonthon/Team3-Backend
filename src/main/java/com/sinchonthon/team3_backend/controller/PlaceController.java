package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.domain.tip.Category;
import com.sinchonthon.team3_backend.dto.response.CategoryResponse;
import com.sinchonthon.team3_backend.dto.response.PlaceMapResponse;
import com.sinchonthon.team3_backend.repository.CategoryRepository;
import com.sinchonthon.team3_backend.repository.PlaceRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceController {
    private final PlaceRepository places;
    private final CategoryRepository categories;

    public PlaceController(PlaceRepository places, CategoryRepository categories) {
        this.places = places;
        this.categories = categories;
    }

    @GetMapping("/api/places/map")
    ApiResponse<List<PlaceMapResponse>> getMapPlaces(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) BigDecimal minLat,
            @RequestParam(required = false) BigDecimal maxLat,
            @RequestParam(required = false) BigDecimal minLng,
            @RequestParam(required = false) BigDecimal maxLng) {
        String categoryName = categoryId == null ? null
                : categories.findById(categoryId).map(Category::getName).orElse(null);

        List<PlaceMapResponse> result = places.findMapPlaces(categoryId, userId, minLat, maxLat, minLng, maxLng)
                .stream()
                .map(p -> new PlaceMapResponse(p.getId(), p.getName(), p.getLatitude(), p.getLongitude(), categoryName))
                .toList();

        return ApiResponse.success(200, "지도 장소 목록 조회 성공", result);
    }

    @GetMapping("/api/categories")
    ApiResponse<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> result = categories.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
        return ApiResponse.success(200, "카테고리 목록 조회 성공", result);
    }
}
