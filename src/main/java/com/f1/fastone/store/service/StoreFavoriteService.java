package com.f1.fastone.store.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreFavorite;
import com.f1.fastone.store.repository.StoreFavoriteRepository;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreFavoriteService {

    private final StoreFavoriteRepository storeFavoriteRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 가게 찜하기
    @Transactional
    public ApiResponse<Void> addFavorite(String username, UUID storeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new ServiceException(ErrorCode.STORE_NOT_FOUND));
        
        // 이미 찜한 가게인지 확인
        if (storeFavoriteRepository.existsByUserAndStoreId(user, storeId)) {
            throw new ServiceException(ErrorCode.ALREADY_FAVORITED);
        }
        
        // 찜하기 등록
        StoreFavorite favorite = StoreFavorite.builder()
                .user(user)
                .store(store)
                .build();
        
        storeFavoriteRepository.save(favorite);
        
        return ApiResponse.success();
    }

    // 가게 찜 취소
    @Transactional
    public ApiResponse<Void> removeFavorite(String username, UUID storeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        
        storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new ServiceException(ErrorCode.STORE_NOT_FOUND));
        
        // 찜한 가게인지 확인
        StoreFavorite favorite = storeFavoriteRepository.findByUserAndStoreId(user, storeId)
                .orElseThrow(() -> new ServiceException(ErrorCode.FAVORITE_NOT_FOUND));
        
        // 찜 취소
        storeFavoriteRepository.delete(favorite);
        
        return ApiResponse.success();
    }

    // 가게 찜 여부 확인
    public ApiResponse<Boolean> isFavorited(String username, UUID storeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        
        boolean isFavorited = storeFavoriteRepository.existsByUserAndStoreId(user, storeId);
        
        return ApiResponse.success(isFavorited);
    }
}
