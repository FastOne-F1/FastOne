package com.f1.fastone.store.repository;

import com.f1.fastone.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    boolean existsByStoreCategoryName(String storeCategoryName);
}