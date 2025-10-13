package com.f1.fastone.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreCategoryUpdateRequestDto(
        @NotBlank(message = "StoreCategory 이름은 필수입니다.")
        @Size(max = 80, message = "StoreCategory 이름은 80자를 초과할 수 없습니다.")
        String storeCategoryName
) {
}
