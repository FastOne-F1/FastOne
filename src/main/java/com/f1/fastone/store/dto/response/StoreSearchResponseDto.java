package com.f1.fastone.store.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchResponseDto {
    
    private UUID storeId;
    private String name;
    private String phone;
    private String city;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isOpen;
    private Long categoryId;
    private String categoryName;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Integer favoriteCount;
    
    // Store 엔티티로부터 응답 DTO 생성
    public static StoreSearchResponseDto fromEntity(com.f1.fastone.store.entity.Store store) {
        return StoreSearchResponseDto.builder()
                .storeId(store.getId())
                .name(store.getName())
                .phone(store.getPhone())
                .city(store.getCity())
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .isOpen(true) // 기본값으로 영업중으로 설정
                .categoryId(store.getCategory() != null ? store.getCategory().getId() : null)
                .categoryName(store.getCategory() != null ? store.getCategory().getStoreCategoryName() : null)
                .averageRating(null) // 추후 StoreRating 연동 시 구현
                .reviewCount(0) // 추후 Review 도메인 연동 시 구현
                .favoriteCount(0) // 추후 수정
                .build();
    }
}
