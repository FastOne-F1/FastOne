package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreSearchRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
import com.f1.fastone.store.dto.response.StoreSearchPageResponseDto;
import com.f1.fastone.store.dto.response.StoreSearchResponseDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreCategoryRepository;
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
        Store savedStore = storeRepository.save(store);

        return ApiResponse.created(StoreResponseDto.fromEntity(savedStore));
    }

    // 단일 가게 조회
    public ApiResponse<StoreResponseDto> getStore(UUID id) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        return ApiResponse.success(StoreResponseDto.fromEntity(store));
    }

    // 모든 가게 목록 조회 (관리자용)
    public ApiResponse<List<StoreResponseDto>> getAllStores() {
        List<Store> stores = storeRepository.findAllByDeletedAtIsNull();
        List<StoreResponseDto> responseDtos = stores.stream()
                .map(StoreResponseDto::fromEntity)
                .toList();

        return ApiResponse.success(responseDtos);
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
        
        // 가장 최근 주소의 city로 가게 조회
        String userCity = userAddresses.get(0).getCity();
        List<Store> stores = storeRepository.findByCityAndDeletedAtIsNull(userCity);
        
        List<StoreResponseDto> responseDtos = stores.stream()
                .map(StoreResponseDto::fromEntity)
                .toList();
        
        return ApiResponse.success(responseDtos);
    }

    // 사용자 주소 기반 가게 검색 및 필터링 (고객용)
    public ApiResponse<StoreSearchPageResponseDto> searchStoresByUserAddress(
            String username, StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 사용자의 주소 조회 (가장 최근 주소)
        List<UserAddress> userAddresses = userAddressRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        
        if (userAddresses.isEmpty()) {
            // 주소가 없으면 빈 결과 반환
            return ApiResponse.success(StoreSearchPageResponseDto.builder()
                    .stores(List.of())
                    .currentPage(0)
                    .totalPages(0)
                    .totalElements(0)
                    .currentPageSize(0)
                    .isFirst(true)
                    .isLast(true)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build());
        }
        
        // 가장 최근 주소의 city로 가게 검색
        String userCity = userAddresses.get(0).getCity();
        return searchStoresByCity(userCity, searchRequest);
    }

    // 특정 도시의 가게 검색 및 필터링
    public ApiResponse<StoreSearchPageResponseDto> searchStoresByCity(
            String city, StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 정렬 설정
        Sort sort = createSort(searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        Page<Store> storePage;
        
        // 검색 조건에 따라 적절한 메서드 호출
        if (searchRequest.hasKeyword() && searchRequest.hasCategoryFilter()) {
            // 키워드 + 카테고리 조합 검색
            storePage = storeRepository.findByNameContainingIgnoreCaseAndCategoryIdAndDeletedAtIsNull(
                    searchRequest.getKeyword(), searchRequest.getCategoryId(), pageable);
        } else if (searchRequest.hasKeyword()) {
            // 키워드만 검색
            storePage = storeRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                    searchRequest.getKeyword(), pageable);
        } else if (searchRequest.hasCategoryFilter()) {
            // 카테고리만 필터링
            storePage = storeRepository.findByCategoryIdAndDeletedAtIsNull(
                    searchRequest.getCategoryId(), pageable);
        } else {
            // 전체 조회 (도시별)
            List<Store> stores = storeRepository.findByCityAndDeletedAtIsNull(city);
            // List를 Page로 변환 (간단한 구현)
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), stores.size());
            List<Store> pageContent = stores.subList(start, end);
            storePage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, stores.size());
        }
        
        // 응답 DTO 변환
        StoreSearchPageResponseDto response = StoreSearchPageResponseDto.fromPage(
                storePage, StoreSearchResponseDto::fromEntity);
        
        return ApiResponse.success(response);
    }

    // 관리자용 전체 가게 검색 및 필터링
    public ApiResponse<StoreSearchPageResponseDto> searchAllStores(StoreSearchRequestDto searchRequest) {
        
        // 검색 요청 유효성 검증 및 기본값 설정
        searchRequest.validateAndSetDefaults();
        
        // 정렬 설정
        Sort sort = createSort(searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        Page<Store> storePage;
        
        // 검색 조건에 따라 적절한 메서드 호출
        if (searchRequest.hasKeyword() && searchRequest.hasCategoryFilter()) {
            // 키워드 + 카테고리 조합 검색
            storePage = storeRepository.findByNameContainingIgnoreCaseAndCategoryIdAndDeletedAtIsNull(
                    searchRequest.getKeyword(), searchRequest.getCategoryId(), pageable);
        } else if (searchRequest.hasKeyword()) {
            // 키워드만 검색
            storePage = storeRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                    searchRequest.getKeyword(), pageable);
        } else if (searchRequest.hasCategoryFilter()) {
            // 카테고리만 필터링
            storePage = storeRepository.findByCategoryIdAndDeletedAtIsNull(
                    searchRequest.getCategoryId(), pageable);
        } else {
            // 전체 조회
            storePage = storeRepository.findAllByDeletedAtIsNull(pageable);
        }
        
        // 응답 DTO 변환
        StoreSearchPageResponseDto response = StoreSearchPageResponseDto.fromPage(
                storePage, StoreSearchResponseDto::fromEntity);
        
        return ApiResponse.success(response);
    }

    // 정렬 설정 생성
    private Sort createSort(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "scoreavg" -> Sort.by(Sort.Direction.DESC, "scoreAvg"); // 추후 StoreRating 연동 시 수정
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // 기본값: 최신순
        };
    }
}
