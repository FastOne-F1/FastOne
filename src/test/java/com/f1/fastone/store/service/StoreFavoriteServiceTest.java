package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreFavorite;
import com.f1.fastone.store.repository.StoreFavoriteRepository;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreFavoriteServiceTest {

    @Mock
    private StoreFavoriteRepository storeFavoriteRepository;
    
    @Mock
    private StoreRepository storeRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private StoreFavoriteService storeFavoriteService;
    
    private User testUser;
    private Store testStore;
    private UUID storeId;
    private String username;
    
    @BeforeEach
    void setUp() {
        username = "testuser";
        storeId = UUID.randomUUID();
        
        testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .role(UserRole.CUSTOMER)
                .build();
        
        testStore = Store.builder()
                .id(storeId)
                .name("테스트 가게")
                .phone("010-1234-5678")
                .city("서울시")
                .address("강남구 테헤란로 123")
                .build();
    }
    
    @Test
    @DisplayName("가게 찜하기 성공")
    void addFavorite_success() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeRepository.findByIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(testStore));
        when(storeFavoriteRepository.existsByUserAndStoreId(testUser, storeId)).thenReturn(false);
        when(storeFavoriteRepository.save(any(StoreFavorite.class))).thenReturn(StoreFavorite.builder().build());
        
        // when
        ApiResponse<Void> response = storeFavoriteService.addFavorite(username, storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        verify(storeFavoriteRepository).save(any(StoreFavorite.class));
    }
    
    @Test
    @DisplayName("가게 찜하기 실패 - 이미 찜한 가게")
    void addFavorite_alreadyFavorited() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeRepository.findByIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(testStore));
        when(storeFavoriteRepository.existsByUserAndStoreId(testUser, storeId)).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> storeFavoriteService.addFavorite(username, storeId))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("이미 찜한 가게입니다");
    }
    
    @Test
    @DisplayName("가게 찜하기 실패 - 사용자 없음")
    void addFavorite_userNotFound() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> storeFavoriteService.addFavorite(username, storeId))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다");
    }
    
    @Test
    @DisplayName("가게 찜하기 실패 - 가게 없음")
    void addFavorite_storeNotFound() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeRepository.findByIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> storeFavoriteService.addFavorite(username, storeId))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("스토어를 찾을 수 없습니다");
    }
    
    @Test
    @DisplayName("가게 찜 취소 성공")
    void removeFavorite_success() {
        // given
        StoreFavorite favorite = StoreFavorite.builder()
                .user(testUser)
                .store(testStore)
                .build();
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeRepository.findByIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(testStore));
        when(storeFavoriteRepository.findByUserAndStoreId(testUser, storeId)).thenReturn(Optional.of(favorite));
        
        // when
        ApiResponse<Void> response = storeFavoriteService.removeFavorite(username, storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        verify(storeFavoriteRepository).delete(favorite);
    }
    
    @Test
    @DisplayName("가게 찜 취소 실패 - 찜하지 않은 가게")
    void removeFavorite_notFavorited() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeRepository.findByIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(testStore));
        when(storeFavoriteRepository.findByUserAndStoreId(testUser, storeId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> storeFavoriteService.removeFavorite(username, storeId))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("찜한 가게가 아닙니다");
    }
    
    @Test
    @DisplayName("가게 찜 여부 확인 성공 - 찜함")
    void isFavorited_true() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeFavoriteRepository.existsByUserAndStoreId(testUser, storeId)).thenReturn(true);
        
        // when
        ApiResponse<Boolean> response = storeFavoriteService.isFavorited(username, storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.data()).isTrue();
    }
    
    @Test
    @DisplayName("가게 찜 여부 확인 성공 - 찜하지 않음")
    void isFavorited_false() {
        // given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(storeFavoriteRepository.existsByUserAndStoreId(testUser, storeId)).thenReturn(false);
        
        // when
        ApiResponse<Boolean> response = storeFavoriteService.isFavorited(username, storeId);
        
        // then
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.data()).isFalse();
    }
}
