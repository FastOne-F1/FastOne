package com.f1.fastone.menu.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.menu.dto.request.MenuCreateRequestDto;
import com.f1.fastone.menu.dto.response.MenuResponseDto;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public ApiResponse<MenuResponseDto> createMenu(MenuCreateRequestDto dto) {
        Store store = storeRepository.findById(dto.storeId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));


    Menu menu = new Menu(
            dto.name(),
            dto.description(),
            dto.price(),
            dto.soldOut(),
            dto.imageUrl(),
            store,
            null
    );

    Menu saved = menuRepository.save(menu);
    return ApiResponse.created(toResponseDto(saved));
    }

    public ApiResponse<MenuResponseDto> getMenu(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        return ApiResponse.success(toResponseDto(menu));
    }

    public ApiResponse<List<MenuResponseDto>> getAllMenus() {
        List<MenuResponseDto> list = menuRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();

        return ApiResponse.success(list);
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
