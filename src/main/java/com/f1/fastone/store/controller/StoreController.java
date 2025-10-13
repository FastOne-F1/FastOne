package com.f1.fastone.store.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "Store", description = "가게 관리 API")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다. CUSTOMER는 OWNER로 승격됩니다.")
    public ResponseEntity<ApiResponse<StoreResponseDto>> createStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid StoreCreateRequestDto request) {

        ApiResponse<StoreResponseDto> response = storeService.createStore(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 조회", description = "특정 ID의 가게 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStore(@PathVariable UUID storeId) {
        ApiResponse<StoreResponseDto> response = storeService.getStore(storeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-area")
    @Operation(summary = "내 지역 가게 조회", description = "사용자 주소 기반으로 해당 지역의 가게 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreResponseDto>>> getStoresByUserAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApiResponse<List<StoreResponseDto>> response = storeService.getStoresByUserAddress(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @Operation(summary = "가게 전체 조회 (관리자용)", description = "관리자만 접근 가능한 전체 가게 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreResponseDto>>> getAllStores() {
        ApiResponse<List<StoreResponseDto>> response = storeService.getAllStores();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 수정", description = "가게 정보를 수정합니다. 본인 가게 또는 관리자만 수정 가능합니다.")
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreUpdateRequestDto request) {

        ApiResponse<StoreResponseDto> response = storeService.updateStore(userDetails.getUsername(), storeId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Operation(summary = "가게 삭제", description = "가게를 삭제합니다. 본인 가게 또는 관리자만 삭제 가능합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId) {

        ApiResponse<Void> response = storeService.deleteStore(userDetails.getUsername(), storeId);
        return ResponseEntity.ok(response);
    }
}
