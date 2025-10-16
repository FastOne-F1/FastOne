package com.f1.fastone.menu.controller;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.menu.dto.request.MenuCreateRequestDto;
import com.f1.fastone.menu.dto.request.MenuUpdateRequestDto;
import com.f1.fastone.menu.dto.response.MenuResponseDto;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.service.MenuService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    // CREATE
    @PostMapping
    public ApiResponse<MenuResponseDto> createMenu(@RequestBody MenuCreateRequestDto dto) {
        return menuService.createMenu(dto);
    }

    // READ 단일
    @GetMapping("/menu/{id}")
    public ApiResponse<MenuResponseDto> getMenu(@PathVariable UUID id) {
        return menuService.getMenu(id);
    }

    // READ 전체
    @GetMapping("/{storeId}")
    public ApiResponse<List<MenuResponseDto>> getMenusByStore(@PathVariable UUID storeId) {
        return menuService.getMenusByStore(storeId);
    }

    @PatchMapping("/{id}/soldout")
    public ApiResponse<MenuResponseDto> updateSoldOutStatus(
            @PathVariable UUID id,
            @RequestParam boolean soldOut
    ) {
        return menuService.updateSoldOutStatus(id, soldOut);
    }

    // PUT
    @PutMapping("/{id}")
    public ApiResponse<MenuResponseDto> updateMenu(@PathVariable UUID id, @RequestBody MenuUpdateRequestDto dto) {
        return menuService.updateMenu(id, dto);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<MenuResponseDto> deleteMenu(@PathVariable UUID id) {
        return menuService.deleteMenu(id);
    }
}
