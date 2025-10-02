package com.f1.fastone.user.controller;

import com.f1.fastone.common.auth.jwt.JwtUtil;
import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.user.dto.SignUpRequestDto;
import com.f1.fastone.user.dto.UserProfileDto;
import com.f1.fastone.user.dto.UserProfileUpdateRequestDto;
import com.f1.fastone.user.repository.UserRepository;
import com.f1.fastone.user.service.RegisterService;
import com.f1.fastone.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "사용자 인증", description = "회원가입, 로그인 관련 API")
public class UserController {

    private final RegisterService registerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<ApiResponse<String>> signup(
            @Valid @RequestBody SignUpRequestDto requestDto) {

        log.info("회원가입 요청: username={}, email={}",
                requestDto.getUsername(), requestDto.getEmail());

        registerService.signup(requestDto);

        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }


    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다 (주소 목록 포함)")
    public ResponseEntity<ApiResponse<UserProfileDto>> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        UserProfileDto userProfile = userProfileService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userProfile));
    }

    @PutMapping("/me")
    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 기본 프로필 정보를 수정합니다 (주소 제외)")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserProfileUpdateRequestDto requestDto) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }
        UserProfileDto updatedProfile = userProfileService.updateMyProfile(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile));
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 삭제(탈퇴)합니다")
    public ResponseEntity<ApiResponse<String>> deleteMyAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            ErrorCode errorCode = ErrorCode.AUTH_INVALID_TOKEN;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null));
        }

        userProfileService.deleteMyAccount(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }



}
