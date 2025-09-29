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

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT 토큰 반환")
    public ResponseEntity<ApiResponse<String>> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response) {

        log.info("로그인 요청: username={}", requestDto.getUsername());

        // 사용자 찾기 & 비밀번호 검증
        Optional<User> userOptional = userRepository.findByUsername(requestDto.getUsername());
        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(requestDto.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(401, "로그인 실패", null));
        }

        User user = userOptional.get();

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        // 쿠키에도 저장
        jwtUtil.addJwtToCookie(token, response);

        log.info("로그인 성공: username={}", user.getUsername());

        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @PostMapping("/test-auth")
    @Operation(summary = "인증 테스트", description = "JWT 토큰 인증을 테스트합니다")
    public ResponseEntity<ApiResponse<String>> testAuth() {
        return ResponseEntity.ok(ApiResponse.success("인증이 성공했습니다."));
    }
}
