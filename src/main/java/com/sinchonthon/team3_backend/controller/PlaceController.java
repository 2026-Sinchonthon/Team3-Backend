package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.domain.tip.Category;
import com.sinchonthon.team3_backend.dto.response.CategoryResponse;
import com.sinchonthon.team3_backend.dto.response.PlaceMapResponse;
import com.sinchonthon.team3_backend.repository.CategoryRepository;
import com.sinchonthon.team3_backend.repository.PlaceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Places", description = "지도 장소 및 카테고리 조회 (인증 불필요)")
@RestController
public class PlaceController {
    private final PlaceRepository places;
    private final CategoryRepository categories;

    public PlaceController(PlaceRepository places, CategoryRepository categories) {
        this.places = places;
        this.categories = categories;
    }

    @Operation(summary = "지도 장소 목록 조회", description = "카테고리/작성자/위경도 범위로 필터링해서 지도 핀에 표시할 장소를 조회합니다.",
            security = {})
    @GetMapping("/api/places/map")
    ApiResponse<List<PlaceMapResponse>> getMapPlaces(
            @Parameter(description = "카테고리 ID로 필터링") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "특정 작성자의 팁이 있는 장소만 조회") @RequestParam(required = false) Long userId,
            @Parameter(description = "위도 최소값") @RequestParam(required = false) BigDecimal minLat,
            @Parameter(description = "위도 최대값") @RequestParam(required = false) BigDecimal maxLat,
            @Parameter(description = "경도 최소값") @RequestParam(required = false) BigDecimal minLng,
            @Parameter(description = "경도 최대값") @RequestParam(required = false) BigDecimal maxLng) {
        String categoryName = categoryId == null ? null
                : categories.findById(categoryId).map(Category::getName).orElse(null);

        List<PlaceMapResponse> result = places.findMapPlaces(categoryId, userId, minLat, maxLat, minLng, maxLng)
                .stream()
                .map(p -> new PlaceMapResponse(p.getId(), p.getName(), p.getLatitude(), p.getLongitude(), categoryName))
                .toList();

        return ApiResponse.success(200, "지도 장소 목록 조회 성공", result);
    }

    @Operation(summary = "카테고리 목록 조회", security = {})
    @GetMapping("/api/categories")
    ApiResponse<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> result = categories.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
        return ApiResponse.success(200, "카테고리 목록 조회 성공", result);
    }
}
