package com.f1.fastone.common.config;


import com.f1.fastone.common.auth.jwt.JwtAuthenticationFilter;
import com.f1.fastone.common.auth.jwt.JwtAuthorizationFilter;
import com.f1.fastone.common.auth.jwt.JwtUtil;
import com.f1.fastone.common.auth.security.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 경로별 권한 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // 정적 리소스 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 회원가입, 로그인 허용
                        .requestMatchers("/user/signup", "/user/login").permitAll()
                        // Swagger UI 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // 관리자 API는 MASTER 권한 필요
                        .requestMatchers("/master/**").hasRole("MASTER")
                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
