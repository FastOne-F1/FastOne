package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreCategoryRepository;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserAddressRepository;
import com.f1.fastone.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceWithStatsTest {

    @Mock
    private StoreRepository storeRepository;
    
    @Mock
    private StoreCategoryRepository storeCategoryRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserAddressRepository userAddressRepository;
    
    @InjectMocks
    private StoreService storeService;
    
    private User testUser;
    private Store testStore;
    private StoreCategory testCategory;
    private UUID storeId;
    
    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.OWNER)
                .build();
        
        testCategory = StoreCategory.builder()
                .id(1L)
                .storeCategoryName("한식")
                .build();
        
        testStore = Store.builder()
                .id(storeId)
                .name("테스트 가게")
                .phone("010-1234-5678")
                .city("서울시")
                .address("강남구 테헤란로 123")
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .isOpen(true)
                .owner(testUser)
                .category(testCategory)
                .build();
    }
    
    @Test
    @DisplayName("단일 가게 조회 시 통계 정보 포함 성공")
    void getStore_withStats_success() {
        // given
        Object[] result = new Object[]{
            testStore,
            5L, // favoriteCount
            new BigDecimal("4.5"), // averageRating
            10 // reviewCount
        };
        
        when(storeRepository.findStoreWithStatsById(storeId)).thenReturn(result);
        
        // when
        ApiResponse<StoreResponseDto> response = storeService.getStore(storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().favoriteCount()).isEqualTo(5L);
        assertThat(response.data().averageRating()).isEqualTo(new BigDecimal("4.5"));
        assertThat(response.data().reviewCount()).isEqualTo(10);
        assertThat(response.data().name()).isEqualTo("테스트 가게");
    }
    
    @Test
    @DisplayName("단일 가게 조회 시 통계 정보가 0인 경우")
    void getStore_withZeroStats_success() {
        // given
        Object[] result = new Object[]{
            testStore,
            0L, // favoriteCount
            new BigDecimal("0.0"), // averageRating
            0 // reviewCount
        };
        
        when(storeRepository.findStoreWithStatsById(storeId)).thenReturn(result);
        
        // when
        ApiResponse<StoreResponseDto> response = storeService.getStore(storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().favoriteCount()).isEqualTo(0L);
        assertThat(response.data().averageRating()).isEqualTo(new BigDecimal("0.0"));
        assertThat(response.data().reviewCount()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("단일 가게 조회 실패 - 가게 없음")
    void getStore_storeNotFound() {
        // given
        when(storeRepository.findStoreWithStatsById(storeId)).thenReturn(null);
        
        // when & then
        assertThatThrownBy(() -> storeService.getStore(storeId))
                .isInstanceOf(com.f1.fastone.common.exception.custom.EntityNotFoundException.class)
                .hasMessageContaining("스토어를 찾을 수 없습니다");
    }
}

