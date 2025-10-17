package com.f1.fastone.store.dto.request;

import com.f1.fastone.store.entity.StoreCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreCategoryCreateRequestDto(
        @NotBlank(message = "StoreCategory 이름은 필수입니다.")
        @Size(max = 80, message = "StoreCategory 이름은 80자를 초과할 수 없습니다.")
        String storeCategoryName
) {
    public StoreCategory toStoreCategoryEntity() {
        return StoreCategory.builder()
                .storeCategoryName(this.storeCategoryName)
                // store 필드 nullable=true이므로 설정하지 않음
                .build();
    }
}