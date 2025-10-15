package com.f1.fastone.menu.service;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.menu.dto.request.MenuCategoryRequestDto;
import com.f1.fastone.menu.dto.response.MenuCategoryResponseDto;
import com.f1.fastone.menu.entity.MenuCategory;
import com.f1.fastone.menu.repository.MenuCategoryRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;

    public MenuCategoryResponseDto createMenuCategory(User user, MenuCategoryRequestDto dto) {
        // 권한 검증
        if (user.getRole() != UserRole.MASTER
                && user.getRole() != UserRole.MANAGER
                && user.getRole() != UserRole.OWNER) {
            throw new ServiceException(ErrorCode.USER_ACCESS_DENIED);
        }


        // 중복 체크
        if (menuCategoryRepository.existsByMenuCategoryName(dto.menuCategoryName())) {
            throw new ServiceException(ErrorCode.MENU_CATEGORY_ALREADY_EXISTS);
        }

        // 저장
        MenuCategory saved = menuCategoryRepository.save(dto.toEntity());

        // DTO 변환 후 반환
        return MenuCategoryResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponseDto> getAllMenuCategories() {
        return menuCategoryRepository.findAll()
                .stream()
                .map(MenuCategoryResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuCategoryResponseDto getMenuCategoryById(UUID id) {
        MenuCategory menuCategory = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_CATEGORY_NOT_FOUND));
        return MenuCategoryResponseDto.fromEntity(menuCategory);
    }

    public MenuCategoryResponseDto updateMenuCategory(User user, UUID id, MenuCategoryRequestDto dto) {
        if (user.getRole() != UserRole.MASTER && user.getRole() != UserRole.MANAGER) {
            throw new ServiceException(ErrorCode.USER_ACCESS_DENIED);
        }

        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_CATEGORY_NOT_FOUND));

        category.updateName(dto.menuCategoryName());

        return MenuCategoryResponseDto.fromEntity(category);
    }

    public void deleteMenuCategory(User user, UUID id) {
        if (user.getRole() != UserRole.MASTER &&  user.getRole() != UserRole.MANAGER) {
            throw new ServiceException(ErrorCode.USER_ACCESS_DENIED);
        }

        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_CATEGORY_NOT_FOUND));

        menuCategoryRepository.delete(category);
    }
}
