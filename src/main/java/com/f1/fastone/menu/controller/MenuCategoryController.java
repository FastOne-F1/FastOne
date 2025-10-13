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

@RestController
@RequestMapping("/menucategories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuCategoryResponseDto>> createMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody MenuCategoryRequestDto dto
            ) {
        MenuCategoryResponseDto response = menuCategoryService.createMenuCategory(userDetails.getUser(), dto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuCategoryResponseDto>>> getAllMenuCategories() {
        List<MenuCategoryResponseDto> categories = menuCategoryService.getAllMenuCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryResponseDto>> getMenuCategoryById(@PathVariable Long id) {
        MenuCategoryResponseDto category = menuCategoryService.getMenuCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryResponseDto>> updateMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Validated @RequestBody MenuCategoryRequestDto dto
    ) {
        MenuCategoryResponseDto response = menuCategoryService.updateMenuCategory(userDetails.getUser(), id, dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMenuCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        menuCategoryService.deleteMenuCategory(userDetails.getUser(), id);
        return ResponseEntity.ok(ApiResponse.success("삭제가 완료되었습니다."));
    }




}
