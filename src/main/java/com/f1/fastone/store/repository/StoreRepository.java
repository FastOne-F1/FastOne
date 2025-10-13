package com.f1.fastone.store.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.f1.fastone.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f1.fastone.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Store findByOwner(User user);
    
    Optional<Store> findByIdAndDeletedAtIsNull(UUID id);
    
    List<Store> findAllByDeletedAtIsNull();
    
    // 사용자 주소의 city와 일치하는 가게 조회
    List<Store> findByCityAndDeletedAtIsNull(String city);

    // 가게명 기반 키워드 검색(페이징)
    Page<Store> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

    // 카테고리별 가게 목록 조회(페이징)
    Page<Store> findByCategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);

    // 특정 가게 카테고리 내의 가게명 검색(페이징)
    Page<Store> findByNameContainingIgnoreCaseAndCategoryIdAndDeletedAtIsNull(
            String name, Long categoryId, Pageable pageable);

    // 관리자용 전체 가게 조회(페이징)
    Page<Store> findAllByDeletedAtIsNull(Pageable pageable);
}