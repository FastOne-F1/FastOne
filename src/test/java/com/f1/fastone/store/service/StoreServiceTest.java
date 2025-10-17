package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreOperatingHoursUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreStatusUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreCategoryRepository;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreCategoryRepository storeCategoryRepository;
    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Store testStore;
    private StoreCategory testCategory;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        testCategory = StoreCategory.builder()
                .id(1L)
                .storeCategoryName("한식")
                .build();

        testStore = Store.builder()
                .id(UUID.randomUUID())
                .name("테스트 가게")
                .phone("010-1234-5678")
                .postalCode("12345")
                .city("Seoul")
                .address("강남구 역삼동")
                .addressDetail("101호")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .owner(testUser)
                .category(testCategory)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    @DisplayName("가게 생성 성공: CUSTOMER → OWNER 승격 포함")
    void createStore_success_customerToOwner() {
        // given
        String username = "testuser";
        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "테스트 가게", "010-1234-5678", "12345", "Seoul",
                "강남구 역삼동", "101호", BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780), LocalTime.of(9, 0),
                LocalTime.of(22, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> result = storeService.createStore(username, requestDto);

        // then
        assertThat(result.data()).isNotNull();
        assertThat(result.data().name()).isEqualTo("테스트 가게");
        assertThat(testUser.getRole()).isEqualTo(UserRole.OWNER);

        then(userRepository).should(times(1)).findByUsername(username);
        then(storeRepository).should(times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 생성 성공: 이미 OWNER인 경우")
    void createStore_success_alreadyOwner() {
        // given
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.OWNER)
                .build();

        String username = "testuser";
        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "테스트 가게", "010-1234-5678", "12345", "Seoul",
                "강남구 역삼동", "101호", BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780), LocalTime.of(9, 0),
                LocalTime.of(22, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> result = storeService.createStore(username, requestDto);

        // then
        assertThat(result.data()).isNotNull();
        assertThat(testUser.getRole()).isEqualTo(UserRole.OWNER);

        then(userRepository).should(times(1)).findByUsername(username);
        then(storeRepository).should(times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 생성 실패: 사용자 없음")
    void createStore_fail_userNotFound() {
        // given
        String username = "nonexistent";
        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "테스트 가게", "010-1234-5678", "12345", "Seoul",
                "강남구 역삼동", "101호", BigDecimal.valueOf(37.5665),
                BigDecimal.valueOf(126.9780), LocalTime.of(9, 0),
                LocalTime.of(22, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.createStore(username, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다");

        then(storeRepository).should(never()).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 단일 조회 성공")
    void getStore_success() {
        // given
        UUID storeId = testStore.getId();
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));

        // when
        ApiResponse<StoreResponseDto> result = storeService.getStore(storeId);

        // then
        assertThat(result.data()).isNotNull();
        assertThat(result.data().name()).isEqualTo("테스트 가게");

        then(storeRepository).should(times(1)).findByIdAndDeletedAtIsNull(storeId);
    }

    @Test
    @DisplayName("가게 단일 조회 실패: 존재하지 않는 ID")
    void getStore_fail_notFound() {
        // given
        UUID nonExistentId = UUID.randomUUID();
        given(storeRepository.findByIdAndDeletedAtIsNull(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("스토어를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("가게 전체 조회 성공")
    void getAllStores_success() {
        // given
        User user1 = User.builder().username("user1").email("user1@test.com").password("password").role(UserRole.CUSTOMER).build();
        User user2 = User.builder().username("user2").email("user2@test.com").password("password").role(UserRole.CUSTOMER).build();
        
        Store store1 = Store.builder().id(UUID.randomUUID()).name("가게1").owner(user1).build();
        Store store2 = Store.builder().id(UUID.randomUUID()).name("가게2").owner(user2).build();
        List<Store> stores = Arrays.asList(store1, store2);

        given(storeRepository.findAllByDeletedAtIsNull()).willReturn(stores);

        // when
        ApiResponse<List<StoreResponseDto>> result = storeService.getAllStores();

        // then
        assertThat(result.data()).hasSize(2);
        assertThat(result.data().get(0).name()).isEqualTo("가게1");
        assertThat(result.data().get(1).name()).isEqualTo("가게2");

        then(storeRepository).should(times(1)).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("가게 수정 성공: owner 본인")
    void updateStore_success_owner() {
        // given
        String username = "testuser";
        UUID storeId = testStore.getId();
        StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(
                "수정된 가게", "010-9999-9999", "54321", "Busan",
                "해운대구", "202호", BigDecimal.valueOf(35.1796),
                BigDecimal.valueOf(129.0756), LocalTime.of(10, 0),
                LocalTime.of(23, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> result = storeService.updateStore(username, storeId, requestDto);

        // then
        assertThat(result.data()).isNotNull();
        assertThat(result.data().name()).isEqualTo("수정된 가게");

        then(userRepository).should(times(1)).findByUsername(username);
        then(storeRepository).should(times(1)).findByIdAndDeletedAtIsNull(storeId);
        then(storeRepository).should(times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 수정 성공: MANAGER")
    void updateStore_success_manager() {
        // given
        User manager = User.builder()
                .username("manager")
                .email("manager@example.com")
                .password("password")
                .role(UserRole.MANAGER)
                .build();

        String username = "manager";
        UUID storeId = testStore.getId();
        StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(
                "수정된 가게", "010-9999-9999", "54321", "Busan",
                "해운대구", "202호", BigDecimal.valueOf(35.1796),
                BigDecimal.valueOf(129.0756), LocalTime.of(10, 0),
                LocalTime.of(23, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(manager));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> result = storeService.updateStore(username, storeId, requestDto);

        // then
        assertThat(result.data()).isNotNull();
        assertThat(result.data().name()).isEqualTo("수정된 가게");

        then(userRepository).should(times(1)).findByUsername(username);
        then(storeRepository).should(times(1)).findByIdAndDeletedAtIsNull(storeId);
        then(storeRepository).should(times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 수정 실패: 권한 없음")
    void updateStore_fail_accessDenied() {
        // given
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        String username = "otheruser";
        UUID storeId = testStore.getId();
        StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(
                "수정된 가게", "010-9999-9999", "54321", "Busan",
                "해운대구", "202호", BigDecimal.valueOf(35.1796),
                BigDecimal.valueOf(129.0756), LocalTime.of(10, 0),
                LocalTime.of(23, 0), null
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(otherUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(username, storeId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 리소스에 접근할 권한이 없습니다");

        then(storeRepository).should(never()).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_success() {
        // given
        String username = "testuser";
        UUID storeId = testStore.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));
        willDoNothing().given(storeRepository).delete(testStore);

        // when
        ApiResponse<Void> result = storeService.deleteStore(username, storeId);

        // then
        assertThat(result.data()).isNull();

        then(userRepository).should(times(1)).findByUsername(username);
        then(storeRepository).should(times(1)).findByIdAndDeletedAtIsNull(storeId);
        then(storeRepository).should(times(1)).delete(testStore);
    }

    @Test
    @DisplayName("가게 삭제 실패: 권한 없음")
    void deleteStore_fail_accessDenied() {
        // given
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        String username = "otheruser";
        UUID storeId = testStore.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(otherUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));

        // when & then
        assertThatThrownBy(() -> storeService.deleteStore(username, storeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 리소스에 접근할 권한이 없습니다");

        then(storeRepository).should(never()).delete(any(Store.class));
    }

    @Test
    @DisplayName("영업 상태 변경 - 성공")
    void updateStoreStatus_success() {
        // given
        String username = "testUser";
        UUID storeId = UUID.randomUUID();
        Boolean newStatus = false;

        User testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(UserRole.OWNER)
                .build();

        Store testStore = Store.builder()
                .id(storeId)
                .name("Test Store")
                .phone("010-1234-5678")
                .owner(testUser)
                .isOpen(true)
                .build();

        StoreStatusUpdateRequestDto requestDto = StoreStatusUpdateRequestDto.builder()
                .isOpen(newStatus)
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> response = storeService.updateStoreStatus(username, storeId, requestDto);

        // then
        assertThat(response.data()).isNotNull();
        
        then(userRepository).should().findByUsername(username);
        then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
        then(storeRepository).should().save(testStore);
    }

    @Test
    @DisplayName("영업 상태 변경 - 권한 없음")
    void updateStoreStatus_accessDenied() {
        // given
        String username = "testUser";
        UUID storeId = UUID.randomUUID();

        User otherUser = User.builder()
                .username("otherUser")
                .email("other@example.com")
                .password("password")
                .role(UserRole.OWNER)
                .build();

        User testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        Store testStore = Store.builder()
                .id(storeId)
                .name("Test Store")
                .owner(otherUser)
                .isOpen(true)
                .build();

        StoreStatusUpdateRequestDto requestDto = StoreStatusUpdateRequestDto.builder()
                .isOpen(false)
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));

        // when & then
        assertThatThrownBy(() -> storeService.updateStoreStatus(username, storeId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 리소스에 접근할 권한이 없습니다");

        then(storeRepository).should(never()).save(any(Store.class));
    }

    @Test
    @DisplayName("영업 시간 변경 - 성공")
    void updateStoreOperatingHours_success() {
        // given
        String username = "testUser";
        UUID storeId = UUID.randomUUID();
        LocalTime newOpenTime = LocalTime.of(9, 0);
        LocalTime newCloseTime = LocalTime.of(22, 0);

        User testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(UserRole.OWNER)
                .build();

        Store testStore = Store.builder()
                .id(storeId)
                .name("Test Store")
                .phone("010-1234-5678")
                .owner(testUser)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(21, 0))
                .build();

        StoreOperatingHoursUpdateRequestDto requestDto = StoreOperatingHoursUpdateRequestDto.builder()
                .openTime(newOpenTime)
                .closeTime(newCloseTime)
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));
        given(storeRepository.save(any(Store.class))).willReturn(testStore);

        // when
        ApiResponse<StoreResponseDto> response = storeService.updateStoreOperatingHours(username, storeId, requestDto);

        // then
        assertThat(response.data()).isNotNull();
        
        then(userRepository).should().findByUsername(username);
        then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
        then(storeRepository).should().save(testStore);
    }

    @Test
    @DisplayName("영업 시간 변경 - 권한 없음")
    void updateStoreOperatingHours_accessDenied() {
        // given
        String username = "testUser";
        UUID storeId = UUID.randomUUID();

        User otherUser = User.builder()
                .username("otherUser")
                .email("other@example.com")
                .password("password")
                .role(UserRole.OWNER)
                .build();

        User testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(UserRole.CUSTOMER)
                .build();

        Store testStore = Store.builder()
                .id(storeId)
                .name("Test Store")
                .owner(otherUser)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(21, 0))
                .build();

        StoreOperatingHoursUpdateRequestDto requestDto = StoreOperatingHoursUpdateRequestDto.builder()
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(testUser));
        given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(testStore));

        // when & then
        assertThatThrownBy(() -> storeService.updateStoreOperatingHours(username, storeId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 리소스에 접근할 권한이 없습니다");

        then(storeRepository).should(never()).save(any(Store.class));
    }
}
