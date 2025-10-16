package com.f1.fastone.store.repository;

import com.f1.fastone.store.dto.response.StoreResponseDto;
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
    
    // 가게 조회 시 StoreRating을 함께 로드 (좋아요 수 등 통계는 별도 쿼리)
    @Query("SELECT s FROM Store s " +
           "LEFT JOIN FETCH s.storeRating sr " +
           "WHERE s.id = :storeId AND s.deletedAt IS NULL")
    Store findStoreWithStatsById(@Param("storeId") UUID storeId);
    
    // 가게 목록 조회 시 좋아요 수와 별점 정보 포함 (페이징)
    @Query("SELECT new com.f1.fastone.store.dto.response.StoreResponseDto(" +
            "s, " +
            "CAST(COALESCE((SELECT COUNT(sf) FROM StoreFavorite sf WHERE sf.store = s), 0) AS long), " +
            "CAST(COALESCE(sr.scoreAvg, 0) AS bigdecimal), " +
            "CAST(COALESCE(sr.reviewCount, 0) AS int)) " +
            "FROM Store s " +
            "LEFT JOIN StoreRating sr ON s.id = sr.store.id " +
            "WHERE s.deletedAt IS NULL")
    Page<StoreResponseDto> findStoresWithStats(Pageable pageable);

    @Query("SELECT new com.f1.fastone.store.dto.response.StoreResponseDto(" +
            "s, " +
            "CAST(COALESCE((SELECT COUNT(sf) FROM StoreFavorite sf WHERE sf.store = s), 0) AS long), " +
            "CAST(COALESCE(sr.scoreAvg, 0) AS bigdecimal), " +
            "CAST(COALESCE(sr.reviewCount, 0) AS int)) " +
            "FROM Store s " +
            "LEFT JOIN FETCH s.category c " +
            "LEFT JOIN FETCH s.owner o " +
            "LEFT JOIN StoreRating sr ON s.id = sr.store.id " +
            "WHERE s.deletedAt IS NULL AND s.city = :city")
    List<StoreResponseDto> findStoresWithStatsByCity(@Param("city") String city, Pageable pageable);

    @Query(
            value = """
                SELECT new com.f1.fastone.store.dto.response.StoreResponseDto(
                    s,
                    CAST(COALESCE((SELECT COUNT(sf) FROM StoreFavorite sf WHERE sf.store = s), 0) AS long),
                    CAST(COALESCE(sr.scoreAvg, 0) AS bigdecimal),
                    CAST(COALESCE(sr.reviewCount, 0) AS int)
                )
                FROM Store s
                LEFT JOIN StoreRating sr ON s.id = sr.store.id
                WHERE s.deletedAt IS NULL
                AND s.city = :city
                AND (:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:categoryId IS NULL OR s.category.id = :categoryId)
            """,
            countQuery = """
                SELECT COUNT(s)
                FROM Store s
                WHERE s.deletedAt IS NULL
                AND s.city = :city
                AND (:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:categoryId IS NULL OR s.category.id = :categoryId)
            """)
    Page<StoreResponseDto> findStoresWithStatsByCityAndFilters(
            @Param("city") String city,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}