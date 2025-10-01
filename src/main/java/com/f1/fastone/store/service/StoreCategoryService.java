package com.f1.fastone.store.service;

import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.store.dto.request.StoreCategoryCreateRequestDto;
import com.f1.fastone.store.dto.request.StoreCategoryUpdateRequestDto;
import com.f1.fastone.store.dto.response.StoreCategoryResponseDto;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreCategoryService {

    private final StoreCategoryRepository storeCategoryRepository;

    // 새로운 StoreCategory를 생성 및 저장
    @Transactional
    public StoreCategoryResponseDto createStoreCategory(StoreCategoryCreateRequestDto request) {

        if (storeCategoryRepository.existsByStoreCategoryName(request.storeCategoryName())) {
            throw new EntityNotFoundException(ErrorCode.STORE_CATEGORY_DUPLICATED, "이미 존재하는 StoreCategory 이름입니다.");
        }

        StoreCategory newStoreCategory = request.toStoreCategoryEntity();
        StoreCategory createdStoreCategory = storeCategoryRepository.save(newStoreCategory);

        return StoreCategoryResponseDto.fromStoreCategoryEntity(createdStoreCategory);
    }

    // StoreCategory 전체 목록 조회
    public List<StoreCategoryResponseDto> findAllStoreCategories() {
        return storeCategoryRepository.findAll().stream()
                .map(StoreCategoryResponseDto::fromStoreCategoryEntity)
                .collect(Collectors.toList());
    }

    // StoreCategory 단일 조회
    public StoreCategoryResponseDto findStoreCategoryById(Long id) {
        StoreCategory storeCategory = storeCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.STORE_CATEGORY_NOT_FOUND,
                        "ID: " + id + "에 해당하는 StoreCategory를 찾을 수 없습니다."
                ));

        return StoreCategoryResponseDto.fromStoreCategoryEntity(storeCategory);
    }

    // 특정 카테고리 수정
    @Transactional
    public StoreCategoryResponseDto updateStoreCategory(
            Long id,
            StoreCategoryUpdateRequestDto request) {

        StoreCategory storeCategory = storeCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ErrorCode.STORE_CATEGORY_NOT_FOUND,
                "ID: " + id + "에 해당하는 StoreCategory를 찾을 수 없어 수정할 수 없습니다."
        ));

        // 카테고리 이름 중복 체크
        if (!storeCategory.getStoreCategoryName().equals(request.storeCategoryName()) &&
                storeCategoryRepository.existsByStoreCategoryName(request.storeCategoryName())) {
            throw new EntityNotFoundException(ErrorCode.STORE_CATEGORY_DUPLICATED, "이미 존재하는 StoreCategory 이름입니다.");
        }

        storeCategory.updateName(request.storeCategoryName());

        StoreCategory updatedCategory = storeCategoryRepository.save(storeCategory);

        return StoreCategoryResponseDto.fromStoreCategoryEntity(updatedCategory);
        // return StoreCategoryResponseDto.fromStoreCategoryEntity(storeCategory); // 이 줄은 제거합니다.
    }

    // 특정 StoreCategory 삭제
    @Transactional
    public void deleteStoreCategoryById(Long id) {
        StoreCategory storeCategory = storeCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorCode.STORE_CATEGORY_NOT_FOUND,
                        "ID: " + id + "에 해당하는 StoreCategory를 찾을 수 없어 삭제할 수 없습니다."
                ));

        storeCategoryRepository.delete(storeCategory);
    }
}