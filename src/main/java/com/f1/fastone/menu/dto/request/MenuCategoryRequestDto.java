package com.f1.fastone.menu.dto.request;

import com.f1.fastone.menu.entity.MenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuCategoryRequestDto(
        @NotBlank(message = "카테고리명은 필수입니다.")
        @Size(max = 80, message = "카테고리명은 최대 80자까지 입력 가능합니다.")
        String menuCategoryName
) {
    public MenuCategory toEntity() {
        return new MenuCategory(menuCategoryName);
    }
}
