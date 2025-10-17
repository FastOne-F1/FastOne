package com.f1.fastone.menu.repository;

import com.f1.fastone.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    boolean existsByMenuCategoryName(String menuCategoryName);
}
