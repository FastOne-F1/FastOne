package com.f1.fastone.store.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 가게 검색 페이징 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchPageResponseDto {
    
    private List<StoreSearchResponseDto> stores;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int currentPageSize;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Spring Data의 Page 객체로부터 응답 DTO 생성
    public static <T> StoreSearchPageResponseDto fromPage(
            org.springframework.data.domain.Page<T> page,
            java.util.function.Function<T, StoreSearchResponseDto> converter) {
        
        List<StoreSearchResponseDto> stores = page.getContent().stream()
                .map(converter)
                .toList();
        
        return StoreSearchPageResponseDto.builder()
                .stores(stores)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPageSize(page.getNumberOfElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
