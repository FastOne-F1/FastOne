package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreOperatingHoursUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreSearchRequestDto;
import com.f1.fastone.store.dto.request.StoreStatusUpdateRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.entity.StoreRating;
import com.f1.fastone.store.repository.StoreCategoryRepository;
import com.f1.fastone.store.repository.StoreFavoriteRepository;
import com.f1.fastone.store.repository.StoreRatingRepository;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserAddressRepository;
import com.f1.fastone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreRatingRepository storeRatingRepository;
    private final StoreFavoriteRepository storeFavoriteRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    // 새로운 가게 생성
    @Transactional
    public ApiResponse<StoreResponseDto> createStore(String username, StoreCreateRequestDto dto) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // CUSTOMER → OWNER 역할 승격
        if (user.getRole() == UserRole.CUSTOMER) {
            user.updateRole(UserRole.OWNER);
        }

        // StoreCategory 조회
        StoreCategory category = null;
        if (dto.categoryId() != null) {
            category = storeCategoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_CATEGORY_NOT_FOUND));
        }

        // Store 생성
        Store store = dto.toEntity(user, category);
        storeRepository.save(store);

        // StoreRating 생성 및 초기화
        StoreRating storeRating = StoreRating.create(store);
        storeRatingRepository.save(storeRating);

        return ApiResponse.created(StoreResponseDto.fromEntity(store));
    }

    // 단일 가게 조회
    public ApiResponse<StoreResponseDto> getStore(UUID id) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));
        Long favoriteCount = storeFavoriteRepository.countByStoreId(store.getId());
        
        return ApiResponse.success(StoreResponseDto.fromEntityWithStats(store, favoriteCount));
    }

    // 모든 가게 목록 조회 (관리자용)
    public ApiResponse<Page<StoreResponseDto>> getAllStores() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<StoreResponseDto> stores = storeRepository.findStoresWithStats(pageable);

        return ApiResponse.success(stores);
    }

    // 가게 정보 수정
    @Transactional
    public ApiResponse<StoreResponseDto> updateStore(String username, UUID id, StoreUpdateRequestDto dto) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Store 조회
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        // 권한 검증: owner 본인 or MANAGER or MASTER
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.MASTER 
            && !store.getOwner().getUsername().equals(username)) {
            throw new EntityNotFoundException(ErrorCode.ACCESS_DENIED);
        }

        // StoreCategory 조회
        StoreCategory category = null;
        if (dto.categoryId() != null) {
            category = storeCategoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_CATEGORY_NOT_FOUND));
        }

        // Store 수정
        store.update(
                dto.name(),
                dto.phone(),
                dto.postalCode(),
                dto.city(),
                dto.address(),
                dto.addressDetail(),
                dto.latitude(),
                dto.longitude(),
                dto.openTime(),
                dto.closeTime(),
                category
        );

        Store updatedStore = storeRepository.save(store);
        return ApiResponse.success(StoreResponseDto.fromEntity(updatedStore));
    }

    // 가게 삭제
    @Transactional
    public ApiResponse<Void> deleteStore(String username, UUID id) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Store 조회
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        // 권한 검증: owner 본인 or MANAGER or MASTER
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.MASTER 
            && !store.getOwner().getUsername().equals(username)) {
            throw new EntityNotFoundException(ErrorCode.ACCESS_DENIED);
        }

        // Hard Delete
        storeRepository.delete(store);
        return ApiResponse.success();
    }

    // 사용자 주소 기반 가게 조회 (고객용)
    public ApiResponse<List<StoreResponseDto>> getStoresByUserAddress(String username) {
        // 사용자의 주소 조회 (가장 최근 주소)
        List<UserAddress> userAddresses = userAddressRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        
        if (userAddresses.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        
        // 가장 최근 주소의 city로 가게 조회 (통계 정보 포함)
        String userCity = userAddresses.get(0).getCity();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<StoreResponseDto> stores = storeRepository.findStoresWithStatsByCity(userCity,pageable);

        return ApiResponse.success(stores);
    }

    // 사용자 주소 기반 가게 검색 및 필터링 (고객용)
    public ApiResponse<Page<StoreResponseDto>> searchStoresByUserAddress(
            String username, StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 사용자의 주소 조회 (가장 최근 주소)
        List<UserAddress> userAddresses = userAddressRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        
        if (userAddresses.isEmpty()) throw new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND, "저장된 주소가 없습니다.");
        
        // 가장 최근 주소의 city로 가게 검색
        String userCity = userAddresses.get(0).getCity();
        return searchStoresByCity(userCity, searchRequest);
    }

    // 특정 도시의 가게 검색 및 필터링
    public ApiResponse<Page<StoreResponseDto>> searchStoresByCity(
            String city, StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 정렬 설정
        Sort sort = createSort(searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        String keyword = searchRequest.getKeyword().toLowerCase();

        // 통계 정보 포함 조회
        Page<StoreResponseDto> storePage = storeRepository.findStoresWithStatsByCityAndFilters(city, keyword, searchRequest.getCategoryId(), pageable);


        
        return ApiResponse.success(storePage);
    }

    // 관리자용 전체 가게 검색 및 필터링
    public ApiResponse<Page<StoreResponseDto>> searchAllStores(StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 정렬 설정
        Sort sort = createSort(searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        Page<StoreResponseDto> storePage = storeRepository.findStoresWithStats(pageable);

        return ApiResponse.success(storePage);
    }

    // 영업 상태 변경
    @Transactional
    public ApiResponse<StoreResponseDto> updateStoreStatus(String username, UUID storeId, StoreStatusUpdateRequestDto dto) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Store 조회
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        // 권한 검증: owner 본인 or MANAGER or MASTER
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.MASTER 
            && !store.getOwner().getUsername().equals(username)) {
            throw new EntityNotFoundException(ErrorCode.ACCESS_DENIED);
        }

        // 영업 상태 변경
        store.updateStatus(dto.getIsOpen());
        Store updatedStore = storeRepository.save(store);

        return ApiResponse.success(StoreResponseDto.fromEntity(updatedStore));
    }

    // 영업 시간 변경
    @Transactional
    public ApiResponse<StoreResponseDto> updateStoreOperatingHours(String username, UUID storeId, StoreOperatingHoursUpdateRequestDto dto) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // Store 조회
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        // 권한 검증: owner 본인 or MANAGER or MASTER
        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.MASTER 
            && !store.getOwner().getUsername().equals(username)) {
            throw new EntityNotFoundException(ErrorCode.ACCESS_DENIED);
        }

        // 영업 시간 변경
        store.updateOperatingHours(dto.getOpenTime(), dto.getCloseTime());
        Store updatedStore = storeRepository.save(store);

        return ApiResponse.success(StoreResponseDto.fromEntity(updatedStore));
    }

    // 정렬 설정 생성
    private Sort createSort(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "scoreavg" -> Sort.by(Sort.Direction.DESC, "storeRating.scoreAvg"); // StoreRating 기준 정렬
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // 기본값: 최신순
        };
    }
}
