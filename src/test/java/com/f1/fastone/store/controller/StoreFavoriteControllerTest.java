package com.f1.fastone.store.controller;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.service.StoreFavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreFavoriteController.class)
class StoreFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreFavoriteService storeFavoriteService;


    private UUID storeId;
    private String username;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        username = "testuser";
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("가게 찜하기 성공")
    void addFavorite_success() throws Exception {
        // given
        when(storeFavoriteService.addFavorite(eq(username), eq(storeId)))
                .thenReturn(ApiResponse.success());

        // when & then
        mockMvc.perform(post("/stores/{storeId}/favorite", storeId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("가게 찜 취소 성공")
    void removeFavorite_success() throws Exception {
        // given
        when(storeFavoriteService.removeFavorite(eq(username), eq(storeId)))
                .thenReturn(ApiResponse.success());

        // when & then
        mockMvc.perform(delete("/stores/{storeId}/favorite", storeId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("가게 찜 여부 확인 성공 - 찜함")
    void isFavorited_true() throws Exception {
        // given
        when(storeFavoriteService.isFavorited(eq(username), eq(storeId)))
                .thenReturn(ApiResponse.success(true));

        // when & then
        mockMvc.perform(get("/stores/{storeId}/favorite", storeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("가게 찜 여부 확인 성공 - 찜하지 않음")
    void isFavorited_false() throws Exception {
        // given
        when(storeFavoriteService.isFavorited(eq(username), eq(storeId)))
                .thenReturn(ApiResponse.success(false));

        // when & then
        mockMvc.perform(get("/stores/{storeId}/favorite", storeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("내가 찜한 가게 목록 조회 성공")
    void getMyFavorites_success() throws Exception {
        // given
        when(storeFavoriteService.getUserFavorites(eq(username)))
                .thenReturn(ApiResponse.success(java.util.List.of()));

        // when & then
        mockMvc.perform(get("/stores/my-favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 시 401")
    void unauthorized_access() throws Exception {
        // when & then
        mockMvc.perform(post("/stores/{storeId}/favorite", storeId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
