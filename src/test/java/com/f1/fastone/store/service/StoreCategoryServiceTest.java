package com.f1.fastone.store.service;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.dto.request.StoreCategoryCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreCategoryUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreCategoryResponseDto;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreCategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class StoreCategoryServiceTest {

    @InjectMocks
    private StoreCategoryService storeCategoryService;

    @Mock
    private StoreCategoryRepository storeCategoryRepository;

    private StoreCategory testCategory;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        testCategory = StoreCategory.builder()
                .id(1L)
                .storeCategoryName("테스트 카테고리")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }


    // 1. CREATE - 새로운 StoreCategory를 생성 및 저장
    @Test
    @DisplayName("카테고리 생성 성공: DB 저장 및 반환 DTO 검증")
    void createStoreCategory_success() {
        String categoryName = "한식";
        StoreCategoryCreateRequestDto requestDto = new StoreCategoryCreateRequestDto(categoryName);

        given(storeCategoryRepository.save(any(StoreCategory.class))).willReturn(testCategory);

        StoreCategoryResponseDto result = storeCategoryService.createStoreCategory(requestDto);

        assertThat(result.id()).isEqualTo(testCategory.getId());
        assertThat(result.storeCategoryName()).isEqualTo(testCategory.getStoreCategoryName());

        then(storeCategoryRepository).should(times(1)).save(any(StoreCategory.class));
    }

    // 2. READ - 특정 StoreCategory 수정
    @Test
    @DisplayName("모든 카테고리 조회 성공: 목록 반환 검증")
    void findAllStoreCategories_success() {
        StoreCategory category1 = StoreCategory.builder().id(1L).storeCategoryName("한식").build();
        StoreCategory category2 = StoreCategory.builder().id(2L).storeCategoryName("일식").build();
        List<StoreCategory> mockList = Arrays.asList(category1, category2);

        given(storeCategoryRepository.findAll()).willReturn(mockList);

        List<StoreCategoryResponseDto> resultList = storeCategoryService.findAllStoreCategories();

        assertThat(resultList).hasSize(2);
        assertThat(resultList.get(0).storeCategoryName()).isEqualTo("한식");
        assertThat(resultList.get(1).id()).isEqualTo(2L);

        then(storeCategoryRepository).should(times(1)).findAll();
    }


    // 3. UPDATE - StoreCategory 전체 목록 조회
    @Test
    @DisplayName("카테고리 수정 성공: Entity updateName 호출 및 save 검증")
    void updateStoreCategory_success() {
        // given
        Long categoryId = 1L;
        String oldName = "기존_카테고리_이름";
        String newName = "새로운 이름";

        StoreCategoryUpdateRequestDto requestDto = new StoreCategoryUpdateRequestDto(newName);

        StoreCategory existingEntity = StoreCategory.builder()
                .id(categoryId)
                .storeCategoryName(oldName)
                .build();

        given(storeCategoryRepository.findById(categoryId)).willReturn(Optional.of(existingEntity));

        given(storeCategoryRepository.existsByStoreCategoryName(newName)).willReturn(false);

        given(storeCategoryRepository.save(any(StoreCategory.class))).willReturn(existingEntity);

        StoreCategoryResponseDto result = storeCategoryService.updateStoreCategory(categoryId, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.storeCategoryName()).isEqualTo(newName);
        assertThat(result.id()).isEqualTo(categoryId);

        then(storeCategoryRepository).should(times(1)).findById(categoryId);
        then(storeCategoryRepository).should(times(1)).existsByStoreCategoryName(newName);
        then(storeCategoryRepository).should(times(1)).save(any(StoreCategory.class)); // ⭐ 이 부분이 이제 성공해야 합니다.
    }

    @Test
    @DisplayName("카테고리 수정 실패: ID 미존재 시 EntityNotFoundException 발생 및 메시지 확인")
    void updateStoreCategory_fail_notFound() {
        Long nonExistentId = 99L;
        StoreCategoryUpdateRequestDto requestDto = new StoreCategoryUpdateRequestDto("새 이름");

        given(storeCategoryRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storeCategoryService.updateStoreCategory(nonExistentId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("StoreCategory");

        then(storeCategoryRepository).should(never()).save(any(StoreCategory.class));
    }


    // 4. DELETE - 특정 StoreCategory 삭제
    @Test
    @DisplayName("카테고리 삭제 성공: Repository delete 메서드 호출 검증")
    void deleteStoreCategoryById_success() {
        Long categoryId = 1L;

        given(storeCategoryRepository.findById(categoryId)).willReturn(Optional.of(testCategory));

        willDoNothing().given(storeCategoryRepository).delete(testCategory);

        storeCategoryService.deleteStoreCategoryById(categoryId);

        then(storeCategoryRepository).should(times(1)).findById(categoryId);
        then(storeCategoryRepository).should(times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("카테고리 삭제 실패: ID 미존재 시 EntityNotFoundException 발생 및 메시지 확인")
    void deleteStoreCategoryById_fail_notFound() {
        Long nonExistentId = 99L;

        given(storeCategoryRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> storeCategoryService.deleteStoreCategoryById(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("StoreCategory");

        then(storeCategoryRepository).should(never()).delete(any(StoreCategory.class));
    }
}