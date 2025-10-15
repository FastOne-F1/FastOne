package com.f1.fastone.order.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
public class OrderSearchRequestDto {

    private String keyword;
    @Min(value = 0, message = "0 페이지부터 조회 가능합니다")
    private int page;
    @Pattern(regexp = "10|30|50", message = "주문은 10/30/50건씩 조회 가능합니다")
    private String size;
    private String sortBy = "createdAt";
    @Pattern(regexp = "asc|desc", message = "정렬 방향은 'asc' 또는 'desc'만 가능합니다.")
    private String sortDirection = "desc";


    public void setUp() {
        // 검색어 모든 공백 제거
        if (this.keyword != null && this.keyword.replaceAll("\\s+", "").isEmpty()) {
            this.keyword = null;
        }
    }
}
