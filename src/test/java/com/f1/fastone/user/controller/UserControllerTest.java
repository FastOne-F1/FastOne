package com.f1.fastone.user.controller;

import com.f1.fastone.common.auth.jwt.JwtUtil;
import com.f1.fastone.user.dto.UserProfileDto;
import com.f1.fastone.user.dto.UserProfileUpdateRequestDto;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("프로필 조회 성공")
    @WithMockUser(username = "testuser", roles = "CUSTOMER")
    void getMyProfile_Success() throws Exception {
        // given
        UserProfileDto userProfile = new UserProfileDto(
                "testuser",
                "테스터",
                "test@example.com",
                "010-1234-5678",
                true,
                UserRole.CUSTOMER,
                List.of()
        );
        given(userProfileService.getMyProfile("testuser")).willReturn(userProfile);

        // when & then
        mockMvc.perform(get("/user/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("테스터"));
    }

    @Test
    @DisplayName("프로필 수정 성공")
    @WithMockUser(username = "testuser", roles = "CUSTOMER")
    void updateMyProfile_Success() throws Exception {
        // given
        UserProfileUpdateRequestDto requestDto = new UserProfileUpdateRequestDto(
                "수정닉네임",
                "010-9999-9999",
                false
        );
        UserProfileDto updatedProfile = new UserProfileDto(
                "testuser",
                "수정닉네임",
                "test@example.com",
                "010-9999-9999",
                false,
                UserRole.CUSTOMER,
                List.of()
        );
        given(userProfileService.updateMyProfile(any(), any())).willReturn(updatedProfile);

        // when & then
        mockMvc.perform(put("/user/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("수정닉네임"));
    }

    @Test
    @DisplayName("CUSTOMER가 관리자 API 접근 시 403")
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void accessAdminApi_Forbidden() throws Exception {
        // when & then
        mockMvc.perform(get("/master/users"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MASTER가 사용자 목록 조회 성공")
    @WithMockUser(username = "master", roles = "MASTER")
    void getAllUsers_Success() throws Exception {
        // given
        List<UserProfileDto> users = Arrays.asList(
                new UserProfileDto("user1", "유저1", "user1@example.com",
                        "010-1111-1111", true, UserRole.CUSTOMER, List.of()),
                new UserProfileDto("user2", "유저2", "user2@example.com",
                        "010-2222-2222", true, UserRole.OWNER, List.of())
        );
        given(userProfileService.findAllUsers()).willReturn(users);

        // when & then
        mockMvc.perform(get("/master/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
