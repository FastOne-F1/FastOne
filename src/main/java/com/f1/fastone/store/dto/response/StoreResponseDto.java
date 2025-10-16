package com.f1.fastone.store.dto.response;

import com.f1.fastone.store.entity.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record StoreResponseDto(
        UUID id,
        String name,
        String phone,
        String postalCode,
        String city,
        String address,
        String addressDetail,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalTime openTime,
        LocalTime closeTime,
        String categoryName,
        String ownerUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // 통계 정보
        Long favoriteCount,
        BigDecimal averageRating,
        Integer reviewCount
) {

    public StoreResponseDto(Store store, Long favoriteCount, BigDecimal averageRating, Integer reviewCount) {
        this(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getPostalCode(),
                store.getCity(),
                store.getAddress(),
                store.getAddressDetail(),
                store.getLatitude(),
                store.getLongitude(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getCategory() != null ? store.getCategory().getStoreCategoryName() : null,
                store.getOwner().getUsername(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                favoriteCount,
                averageRating,
                reviewCount
        );
    }

    public static StoreResponseDto fromEntity(Store store) {
        return new StoreResponseDto(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getPostalCode(),
                store.getCity(),
                store.getAddress(),
                store.getAddressDetail(),
                store.getLatitude(),
                store.getLongitude(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getCategory() != null ? store.getCategory().getStoreCategoryName() : null,
                store.getOwner().getUsername(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                // 통계 정보는 null로 설정 (기존 호환성 유지)
                null, null, null
        );
    }
    
    public static StoreResponseDto fromEntityWithStats(Store store, Long favoriteCount) {
        return new StoreResponseDto(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getPostalCode(),
                store.getCity(),
                store.getAddress(),
                store.getAddressDetail(),
                store.getLatitude(),
                store.getLongitude(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getCategory() != null ? store.getCategory().getStoreCategoryName() : null,
                store.getOwner().getUsername(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                favoriteCount,
                store.getStoreRating().getScoreAvg(),
                store.getStoreRating().getReviewCount()
        );
    }
}
