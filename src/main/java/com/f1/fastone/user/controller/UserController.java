package com.f1.fastone.user.controller;

import com.f1.fastone.common.auth.jwt.JwtUtil;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.user.dto.LoginRequestDto;
import com.f1.fastone.user.dto.SignUpRequestDto;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.repository.UserRepository;
import com.f1.fastone.user.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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


}
