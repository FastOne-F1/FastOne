package com.f1.fastone.store.repository;

import com.f1.fastone.store.entity.StoreFavorite;
import com.f1.fastone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreFavoriteRepository extends JpaRepository<StoreFavorite, Long> {
    
    // 특정 사용자와 가게의 좋아요 관계 조회
    Optional<StoreFavorite> findByUserAndStoreId(User user, UUID storeId);
    
    // 특정 사용자의 모든 좋아요 목록 조회
    List<StoreFavorite> findByUser(User user);
    
    // 특정 가게의 좋아요 수 조회
    @Query("SELECT COUNT(sf) FROM StoreFavorite sf WHERE sf.store.id = :storeId")
    long countByStoreId(@Param("storeId") UUID storeId);
    
    // 특정 사용자가 특정 가게를 좋아요 했는지 확인
    boolean existsByUserAndStoreId(User user, UUID storeId);
}
