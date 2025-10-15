package com.f1.fastone.user.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.user.dto.UserAddressCreateRequestDto;
import com.f1.fastone.user.dto.UserAddressDto;
import com.f1.fastone.user.dto.UserAddressUpdateRequestDto;
import com.f1.fastone.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addresses")
@RequiredArgsConstructor
@Tag(name = "address-controller", description = "사용자 주소 관리 API")
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    @Operation(summary = "내 주소 목록 조회", description = "로그인한 사용자의 모든 주소를 조회합니다")
    public ResponseEntity<ApiResponse<List<UserAddressDto>>> getMyAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        List<UserAddressDto> addresses = userAddressService.getMyAddresses(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping
    @Operation(summary = "주소 추가", description = "새로운 주소를 추가합니다")
    public ResponseEntity<ApiResponse<UserAddressDto>> addAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserAddressCreateRequestDto requestDto) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        UserAddressDto newAddress = userAddressService.addAddress(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(newAddress));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "주소 수정", description = "기존 주소를 수정합니다")
    public ResponseEntity<ApiResponse<UserAddressDto>> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody UserAddressUpdateRequestDto requestDto) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        UserAddressDto updatedAddress = userAddressService.updateAddress(userDetails.getUsername(), addressId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedAddress));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "주소 삭제", description = "기존 주소를 삭제합니다")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long addressId) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        userAddressService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.ok(ApiResponse.success("주소가 삭제되었습니다."));
    }
}
