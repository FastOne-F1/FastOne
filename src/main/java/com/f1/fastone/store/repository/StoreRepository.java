package com.f1.fastone.store.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.f1.fastone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f1.fastone.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Store findByOwner(User user);
    
    Optional<Store> findByIdAndDeletedAtIsNull(UUID id);
    
    List<Store> findAllByDeletedAtIsNull();
    
    // 사용자 주소의 city와 일치하는 가게 조회
    List<Store> findByCityAndDeletedAtIsNull(String city);
}