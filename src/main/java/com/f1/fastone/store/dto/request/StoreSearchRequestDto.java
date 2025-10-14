package com.f1.fastone.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchRequestDto {

    // 가게 검색 요청 DTO
    // 검색: 상호명, 주소
    // 필터링: 카테고리별, 운영상태별 필터링
    // 페이징, 정렬
    
    @Size(max = 100, message = "검색어는 100자를 초과할 수 없습니다.")
    private String keyword;
    
    private Long categoryId;
    
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private Integer page;
    
    @Min(value = 10, message = "페이지 크기는 최소 10입니다.")
    @Max(value = 50, message = "페이지 크기는 최대 50입니다.")
    private Integer size;
    
    private String sortBy;
    
    // 기본값 설정 및 유효성 검증을 위한 메서드
    public void validateAndSetDefaults() {
    
        // 페이지 번호 기본값 설정
        if (this.page == null) {
            this.page = 0;
        }
        
        // 페이지 크기 기본값 설정 및 유효성 검증
        if (this.size == null) {
            this.size = 10;
        } else {
            // 10, 30, 50 중 하나가 아니면 10으로 설정
            if (this.size != 10 && this.size != 30 && this.size != 50) {
                this.size = 10;
            }
        }
        
        // 정렬 기준 기본값 설정
        if (this.sortBy == null || this.sortBy.trim().isEmpty()) {
            this.sortBy = "createdAt";
        }
        
        // 검색어 공백 처리
        if (this.keyword != null) {
            this.keyword = this.keyword.trim();
            if (this.keyword.isEmpty()) {
                this.keyword = null;
            }
        }
    }
    
    // 검색어가 있는지 확인
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    // 카테고리 필터가 있는지 확인
    public boolean hasCategoryFilter() {
        return categoryId != null;
    }
    
}
