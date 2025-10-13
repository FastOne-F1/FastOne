package com.f1.fastone.menu.dto.response;

import com.f1.fastone.menu.entity.MenuCategory;

import java.util.UUID;

public record MenuCategoryResponseDto(UUID id, String menuCategoryName) {
    public static MenuCategoryResponseDto fromEntity(MenuCategory menuCategory) {
        return new MenuCategoryResponseDto(menuCategory.getId(), menuCategory.getMenuCategoryName());
    }
}
