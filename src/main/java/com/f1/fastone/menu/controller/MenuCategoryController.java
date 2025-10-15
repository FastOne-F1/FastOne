package com.f1.fastone.menu.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.menu.dto.request.MenuCategoryRequestDto;
import com.f1.fastone.menu.dto.response.MenuCategoryResponseDto;
import com.f1.fastone.menu.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/menucategories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping
    public ApiResponse<MenuCategoryResponseDto> createMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody MenuCategoryRequestDto dto
            ) {
        MenuCategoryResponseDto response = menuCategoryService.createMenuCategory(userDetails.getUser(), dto);

        return ApiResponse.created(response);
    }

    @GetMapping
    public ApiResponse<List<MenuCategoryResponseDto>> getAllMenuCategories() {
        List<MenuCategoryResponseDto> categories = menuCategoryService.getAllMenuCategories();
        return ApiResponse.success(categories);
    }

    @GetMapping("/{id}")
    public ApiResponse<MenuCategoryResponseDto> getMenuCategoryById(@PathVariable UUID id) {
        MenuCategoryResponseDto category = menuCategoryService.getMenuCategoryById(id);
        return ApiResponse.success(category);
    }

    @PutMapping("/{id}")
    public ApiResponse<MenuCategoryResponseDto> updateMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id,
            @Validated @RequestBody MenuCategoryRequestDto dto
    ) {
        MenuCategoryResponseDto response = menuCategoryService.updateMenuCategory(userDetails.getUser(), id, dto);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id
    ) {
        menuCategoryService.deleteMenuCategory(userDetails.getUser(), id);
        return ApiResponse.success("삭제가 완료되었습니다.");
    }
}
