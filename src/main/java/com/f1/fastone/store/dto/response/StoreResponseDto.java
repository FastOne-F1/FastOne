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
        LocalDateTime updatedAt
) {
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
                store.getUpdatedAt()
        );
    }
}
