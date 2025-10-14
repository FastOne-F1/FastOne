package com.f1.fastone.menu.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.menu.dto.request.MenuCreateRequestDto;
import com.f1.fastone.menu.dto.request.MenuUpdateRequestDto;
import com.f1.fastone.menu.dto.response.MenuResponseDto;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.entity.MenuCategory;
import com.f1.fastone.menu.repository.MenuCategoryRepository;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository  menuCategoryRepository;
    private final StoreRepository storeRepository;

    // 메뉴 생성
    public ApiResponse<MenuResponseDto> createMenu(MenuCreateRequestDto dto) {
        Store store = storeRepository.findById(dto.storeId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));
        
        MenuCategory category = menuCategoryRepository.findById(dto.categoryId())
              .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_CATEGORY_NOT_FOUND));



    Menu menu = new Menu(
            dto.name(),
            dto.description(),
            dto.price(),
            dto.soldOut(),
            dto.imageUrl(),
            store,
            category
    );

    Menu saved = menuRepository.save(menu);
    return ApiResponse.created(toResponseDto(saved));
    }

    // 단일 메뉴 조회
    public ApiResponse<MenuResponseDto> getMenu(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        return ApiResponse.success(toResponseDto(menu));
    }

    // 특정 가게의 메뉴 전체 조회
    public ApiResponse<List<MenuResponseDto>> getMenusByStore(UUID storeId) {
        List<MenuResponseDto> list = menuRepository.findByStoreId(storeId).stream()
                .map(this::toResponseDto)
                .toList();

        return ApiResponse.success(list);
    }

    // 메뉴 전체 수정
    public ApiResponse<MenuResponseDto> updateMenu(UUID id, MenuUpdateRequestDto dto) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        MenuCategory category = null;
        if (dto.categoryId() != null) {
            category = menuCategoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_CATEGORY_NOT_FOUND));
        }

        menu.update(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.soldOut(),
                dto.imageUrl(),
                category
        );

        Menu updated = menuRepository.save(menu);
        return ApiResponse.success(toResponseDto(updated));
    }

    // 메뉴 품절 여부 수정
    public ApiResponse<MenuResponseDto> updateSoldOutStatus(UUID id, boolean soldOut) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        menu.updateSoldOut(soldOut);

        Menu updated = menuRepository.save(menu);
        return ApiResponse.success(toResponseDto(updated));

    }

    // 메뉴 삭제
    public ApiResponse<MenuResponseDto> deleteMenu(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        menuRepository.delete(menu);
        return ApiResponse.success(toResponseDto(menu));
    }


    private MenuResponseDto toResponseDto(Menu menu) {
        return new MenuResponseDto(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.isSoldOut(),
                menu.getImageUrl()
        );
    }
}
