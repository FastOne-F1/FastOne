package com.f1.fastone.common.auth.filter;

import com.f1.fastone.common.auth.jwt.JwtUtil;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.user.dto.LoginRequestDto;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "LoginFilter")
@Component
@Order(1)
public class LoginFilter implements Filter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public LoginFilter(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // POST /user/login 요청만 처리
        if ("POST".equals(method) && "/user/login".equals(url)) {
            handleLogin(httpRequest, httpResponse);
            return; // 필터 체인 중단
        }

        // 다른 요청은 다음 필터로
        chain.doFilter(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Request Body 읽기
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            log.info("로그인 요청: username={}", loginRequestDto.getUsername());

            // 사용자 검증
            Optional<User> userOptional = userRepository.findByUsername(loginRequestDto.getUsername());
            if (userOptional.isEmpty() ||
                    !passwordEncoder.matches(loginRequestDto.getPassword(), userOptional.get().getPassword())) {

                sendErrorResponse(response, 401, "로그인 실패");
                return;
            }

            User user = userOptional.get();

            // JWT 토큰 생성
            String token = jwtUtil.createToken(user.getUsername(), user.getRole());

            // 쿠키에 저장
            jwtUtil.addJwtToCookie(token, response);

            // 성공 응답
            sendSuccessResponse(response, token);

            log.info("로그인 성공: username={}", user.getUsername());

        } catch (Exception e) {
            log.error("로그인 처리 중 오류: {}", e.getMessage());
            sendErrorResponse(response, 500, "서버 내부 오류");
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, String token) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<String> apiResponse = ApiResponse.success(token);
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<String> apiResponse = new ApiResponse<>(status, message, null);
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
    }
}
