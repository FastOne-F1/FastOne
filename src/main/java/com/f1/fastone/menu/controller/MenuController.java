package com.f1.fastone.menu.controller;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.menu.dto.request.MenuCreateRequestDto;
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

    @PostMapping
    public ApiResponse<MenuResponseDto> createMenu(@RequestBody MenuCreateRequestDto dto) {
        return menuService.createMenu(dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<MenuResponseDto> getMenu(@PathVariable UUID id) {
        return menuService.getMenu(id);
    }

    @GetMapping
    public ApiResponse<List<MenuResponseDto>> getAllMenus() {
        return menuService.getAllMenus();
    }

}
