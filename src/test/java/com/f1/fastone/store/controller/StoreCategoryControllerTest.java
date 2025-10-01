package com.f1.fastone.store.controller;

import com.f1.fastone.store.dto.request.StoreCategoryCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreCategoryUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreCategoryResponseDto;
import com.f1.fastone.store.service.StoreCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class StoreCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreCategoryService storeCategoryService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        StoreCategoryService storeCategoryService() {
            return Mockito.mock(StoreCategoryService.class);
        }
    }


    // 1. CREATE - 새로운 StoreCategory를 생성 및 저장
    @Test
    @DisplayName("[POST] MASTER 권한: StoreCategory 생성 성공")
    @WithMockUser(roles = {"MASTER"})
    void createStoreCategory_success_withMasterRole() throws Exception {
        String categoryName = "한식";
        StoreCategoryCreateRequestDto requestDto = new StoreCategoryCreateRequestDto(categoryName);
        StoreCategoryResponseDto mockResponse = new StoreCategoryResponseDto(1L, categoryName);

        Mockito.when(storeCategoryService.createStoreCategory(any(StoreCategoryCreateRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/store-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.storeCategoryName").value(categoryName));
    }

    @Test
    @DisplayName("[POST] 일반 사용자 권한: 403 Forbidden 반환")
    @WithMockUser(roles = {"CUSTOMER"})
    void createStoreCategory_fail_withCustomerRole() throws Exception {

        String requestJson = "{\"storeCategoryName\": \"중식\"}";

        mockMvc.perform(post("/store-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    // 2. READ - 특정 StoreCategory 수정
    @Test
    @DisplayName("[GET] StoreCategory 전체 목록 조회 성공")
    void getAllStoreCategories_success() throws Exception {
        List<StoreCategoryResponseDto> mockList = List.of(
                new StoreCategoryResponseDto(1L, "한식"),
                new StoreCategoryResponseDto(2L, "일식")
        );

        Mockito.when(storeCategoryService.findAllStoreCategories()).thenReturn(mockList);

        mockMvc.perform(get("/store-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].storeCategoryName").value("한식"));
    }

    // 3. UPDATE - StoreCategory 전체 목록 조회
    @Test
    @DisplayName("[PUT] MASTER 권한: StoreCategory 수정 성공")
    @WithMockUser(roles = {"MASTER"})
    void updateStoreCategory_success_withMasterRole() throws Exception {
        Long testId = 1L;
        String updateName = "수정된 카테고리";
        StoreCategoryUpdateRequestDto requestDto = new StoreCategoryUpdateRequestDto(updateName);
        StoreCategoryResponseDto mockResponse = new StoreCategoryResponseDto(testId, updateName);

        Mockito.when(storeCategoryService.updateStoreCategory(eq(testId), any(StoreCategoryUpdateRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/store-categories/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storeCategoryName").value(updateName));
    }

    @Test
    @DisplayName("[PUT] 일반 사용자 권한: 403 Forbidden 반환")
    @WithMockUser(roles = {"CUSTOMER"})
    void updateStoreCategory_fail_withCustomerRole() throws Exception {
        Long testId = 1L;
        String requestJson = "{\"storeCategoryName\": \"업데이트\"}";

        mockMvc.perform(put("/store-categories/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    // 4. DELETE - 특정 StoreCategory 삭제
    @Test
    @DisplayName("[DELETE] MASTER 권한: StoreCategory 삭제 성공")
    @WithMockUser(roles = {"MASTER"})
    void deleteStoreCategory_success_withMasterRole() throws Exception {
        Long testId = 1L;
        Mockito.doNothing().when(storeCategoryService).deleteStoreCategoryById(testId);

        mockMvc.perform(delete("/store-categories/{id}", testId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(storeCategoryService).deleteStoreCategoryById(testId); // 검증 추가
    }

    @Test
    @DisplayName("[DELETE] 일반 사용자 권한: 403 Forbidden 반환")
    @WithMockUser(roles = {"CUSTOMER"})
    void deleteStoreCategory_fail_withCustomerRole() throws Exception {
        Long testId = 1L;

        // When & Then
        mockMvc.perform(delete("/store-categories/{id}", testId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}