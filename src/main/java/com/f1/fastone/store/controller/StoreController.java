package com.f1.fastone.store.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreOperatingHoursUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreSearchRequestDto;
import com.f1.fastone.store.dto.request.StoreStatusUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.dto.response.StoreSearchPageResponseDto;
import com.f1.fastone.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    // 새로운 가게 등록
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다. CUSTOMER는 OWNER로 승격됩니다.")
    public ApiResponse<StoreResponseDto> createStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid StoreCreateRequestDto request) {
        ApiResponse<StoreResponseDto> response = storeService.createStore(userDetails.getUsername(), request);
        return ApiResponse.created(response.data());
    }

    // 가게 단일 조회
    @GetMapping("/{storeId}")
    @Operation(summary = "가게 조회", description = "특정 ID의 가게 정보를 조회합니다.")
    public ApiResponse<StoreResponseDto> getStore(@PathVariable UUID storeId) {
        ApiResponse<StoreResponseDto> response = storeService.getStore(storeId);
        return ApiResponse.success(response.data());
    }

    // 내 지역 가게 목록 조회
    @GetMapping("/my-area")
    @Operation(summary = "내 지역 가게 조회", description = "사용자 주소 기반으로 해당 지역의 가게 목록을 조회합니다.")
    public ApiResponse<List<StoreResponseDto>> getStoresByUserAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApiResponse<List<StoreResponseDto>> response = storeService.getStoresByUserAddress(userDetails.getUsername());
        return ApiResponse.success(response.data());
    }

    // 내 지역 가게 검색 및 필터링 (고객용)
    @GetMapping("/search")
    @Operation(summary = "가게 검색 (고객용)", 
               description = "사용자 주소 기반으로 가게를 검색합니다. 키워드, 카테고리, 페이징, 정렬을 지원합니다.")
    public ApiResponse<StoreSearchPageResponseDto> searchStores(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "검색 키워드 (가게명)", example = "BBQ")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "카테고리 ID", example = "1")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "페이지 크기 (10, 30, 50)", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            
            @Parameter(description = "정렬 기준 (createdAt, scoreAvg)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        StoreSearchRequestDto searchRequest = StoreSearchRequestDto.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .build();
        
        ApiResponse<StoreSearchPageResponseDto> response = storeService.searchStoresByUserAddress(
                userDetails.getUsername(), searchRequest);
        return ApiResponse.success(response.data());
    }

    // 전체 가게 목록 조회
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Operation(summary = "가게 전체 조회 (관리자용)", description = "관리자만 접근 가능한 전체 가게 목록을 조회합니다.")
    public ApiResponse<List<StoreResponseDto>> getAllStores() {
        ApiResponse<List<StoreResponseDto>> response = storeService.getAllStores();
        return ApiResponse.success(response.data());
    }

    // 관리자용 전체 가게 검색 및 필터링
    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Operation(summary = "가게 검색 (관리자용)", 
               description = "관리자만 접근 가능한 전체 가게 검색입니다. 키워드, 카테고리, 페이징, 정렬을 지원합니다.")
    public ApiResponse<StoreSearchPageResponseDto> searchAllStores(
            @Parameter(description = "검색 키워드 (가게명)", example = "치킨")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "카테고리 ID", example = "2")
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            
            @Parameter(description = "페이지 크기 (10, 30, 50)", example = "30")
            @RequestParam(defaultValue = "10") Integer size,
            
            @Parameter(description = "정렬 기준 (createdAt, scoreAvg)", example = "scoreAvg")
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        StoreSearchRequestDto searchRequest = StoreSearchRequestDto.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .build();
        
        ApiResponse<StoreSearchPageResponseDto> response = storeService.searchAllStores(searchRequest);
        return ApiResponse.success(response.data());
    }

    // 가게 정보 수정
    @PutMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 수정", description = "가게 정보를 수정합니다. 본인 가게 또는 관리자만 수정 가능합니다.")
    public ApiResponse<StoreResponseDto> updateStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreUpdateRequestDto request) {
        ApiResponse<StoreResponseDto> response = storeService.updateStore(userDetails.getUsername(), storeId, request);
        return ApiResponse.success(response.data());
    }

    // 가게 삭제
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 삭제", description = "가게를 삭제합니다. 본인 가게 또는 관리자만 삭제 가능합니다.")
    public ApiResponse<Void> deleteStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId) {
        storeService.deleteStore(userDetails.getUsername(), storeId);
        return ApiResponse.success();
    }

    // 가게 영업 상태 변경
    @PutMapping("/{storeId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 영업 상태 변경", description = "가게의 영업 상태(오픈/클로즈)를 변경합니다. 본인 가게 또는 관리자만 변경 가능합니다.")
    public ApiResponse<StoreResponseDto> updateStoreStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreStatusUpdateRequestDto request) {
        ApiResponse<StoreResponseDto> response = storeService.updateStoreStatus(
                userDetails.getUsername(), storeId, request);
        return ApiResponse.success(response.data());
    }

    // 가게 영업 시간 변경
    @PutMapping("/{storeId}/operating-hours")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 영업 시간 변경", description = "가게의 영업 시간(오픈시간/마감시간)을 변경합니다. 본인 가게 또는 관리자만 변경 가능합니다.")
    public ApiResponse<StoreResponseDto> updateStoreOperatingHours(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreOperatingHoursUpdateRequestDto request) {
        ApiResponse<StoreResponseDto> response = storeService.updateStoreOperatingHours(
                userDetails.getUsername(), storeId, request);
        return ApiResponse.success(response.data());
    }
}
