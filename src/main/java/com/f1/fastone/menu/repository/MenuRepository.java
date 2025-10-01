package com.f1.fastone.menu.repository;

import com.f1.fastone.menu.entity.Menu;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Optional<Menu> findByIdAndStoreId(UUID id, UUID store_id);
}