package com.f1.fastone.store.dto.response;

import com.f1.fastone.store.entity.StoreCategory;

public record StoreCategoryResponseDto(
        Long id,
        String storeCategoryName
) {
    public static StoreCategoryResponseDto fromStoreCategoryEntity(StoreCategory storeCategoryEntity) {
        return new StoreCategoryResponseDto(
                storeCategoryEntity.getId(),
                storeCategoryEntity.getStoreCategoryName()
        );
    }
}