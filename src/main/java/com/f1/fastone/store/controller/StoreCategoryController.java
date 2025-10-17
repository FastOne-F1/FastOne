package com.f1.fastone.store.controller;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.dto.request.StoreCategoryCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreCategoryUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreCategoryResponseDto;
import com.f1.fastone.store.service.StoreCategoryService;
import io.swagger.v3.oas.annotations.Operation;
 
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store-categories")
public class StoreCategoryController {

    private final StoreCategoryService storeCategoryService;

    // 새로운 StoreCategory를 생성 및 저장
    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "StoreCategory 등록", description = "관리자(MASTER)가 새로운 StoreCategory를 등록합니다.")
    public ApiResponse<StoreCategoryResponseDto> createStoreCategory(
            @RequestBody @Valid StoreCategoryCreateRequestDto request) {

        StoreCategoryResponseDto response = storeCategoryService.createStoreCategory(request);

        return ApiResponse.created(response);
    }

    // 특정 StoreCategory 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "StoreCategory 수정", description = "관리자(MASTER)만 특정 ID의 StoreCategory 이름을 수정합니다.")
    public ApiResponse<StoreCategoryResponseDto> updateStoreCategory(
            @PathVariable Long id,
            @RequestBody @Valid StoreCategoryUpdateRequestDto request) {

        StoreCategoryResponseDto response = storeCategoryService.updateStoreCategory(id, request);

        return ApiResponse.success(response);
    }

    // StoreCategory 전체 목록 조회
    @GetMapping
    @Operation(summary = "StoreCategory 전체 조회", description = "등록된 모든 StoreCategory 목록을 조회합니다.")
    public ApiResponse<List<StoreCategoryResponseDto>> getAllStoreCategories() {

        List<StoreCategoryResponseDto> response = storeCategoryService.findAllStoreCategories();

        return ApiResponse.success(response);
    }

    // 특정 StoreCategory 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "StoreCategory 삭제", description = "관리자(MASTER)가 특정 ID의 StoreCategory를 삭제합니다.")
    public ApiResponse<Void> deleteStoreCategoryById(@PathVariable Long id) {

        storeCategoryService.deleteStoreCategoryById(id);

        return ApiResponse.success();
    }
}