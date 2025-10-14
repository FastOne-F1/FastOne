package com.f1.fastone.store.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.service.StoreFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreFavoriteController {

    private final StoreFavoriteService storeFavoriteService;

    // 가게 찜하기
    @PostMapping("/{storeId}/favorite")
    @Operation(summary = "가게 찜하기", description = "특정 가게를 찜 목록에 추가합니다.")
    public ApiResponse<Void> addFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "가게 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID storeId) {
        
        return storeFavoriteService.addFavorite(userDetails.getUsername(), storeId);
    }

    // 가게 찜 취소
    @DeleteMapping("/{storeId}/favorite")
    @Operation(summary = "가게 찜 취소", description = "특정 가게를 찜 목록에서 제거합니다.")
    public ApiResponse<Void> removeFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "가게 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID storeId) {
        
        return storeFavoriteService.removeFavorite(userDetails.getUsername(), storeId);
    }

    // 가게 찜 여부 확인
    @GetMapping("/{storeId}/favorite")
    @Operation(summary = "가게 찜 여부 확인", description = "특정 가게를 찜했는지 확인합니다.")
    public ApiResponse<Boolean> isFavorited(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "가게 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID storeId) {
        
        return storeFavoriteService.isFavorited(userDetails.getUsername(), storeId);
    }
}
