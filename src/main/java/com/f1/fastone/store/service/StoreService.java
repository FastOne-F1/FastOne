package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.dto.request.StoreCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreResponseDto;
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

    @Transactional
    public ApiResponse<StoreResponseDto> createStore(String username, StoreCreateRequestDto dto) {
        // User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // CUSTOMER → OWNER 역할 승격
        if (user.getRole() == UserRole.CUSTOMER) {
            user.updateRole(UserRole.OWNER);
        }

        // StoreCategory 조회 (선택사항)
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

    public ApiResponse<StoreResponseDto> getStore(UUID id) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        return ApiResponse.success(StoreResponseDto.fromEntity(store));
    }

    public ApiResponse<List<StoreResponseDto>> getAllStores() {
        List<Store> stores = storeRepository.findAllByDeletedAtIsNull();
        List<StoreResponseDto> responseDtos = stores.stream()
                .map(StoreResponseDto::fromEntity)
                .toList();

        return ApiResponse.success(responseDtos);
    }

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

        // StoreCategory 조회 (선택사항)
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
}
