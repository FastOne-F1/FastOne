package com.f1.fastone.menu.service;

import com.f1.fastone.menu.dto.request.MenuCategoryRequestDto;
import com.f1.fastone.menu.dto.response.MenuCategoryResponseDto;
import com.f1.fastone.menu.repository.MenuCategoryRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MenuCategoryServiceTest {

    @Autowired
    private MenuCategoryService menuCategoryService;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Test
    @DisplayName("관리자가 메뉴 카테고리를 생성할 수 있다")
    void createMenuCategoryTest() {
        // given
        User admin = org.mockito.Mockito.mock(User.class);
        org.mockito.Mockito.when(admin.getRole()).thenReturn(UserRole.MASTER);

        MenuCategoryRequestDto dto = new MenuCategoryRequestDto("테스트 카테고리");

        // when
        MenuCategoryResponseDto response = menuCategoryService.createMenuCategory(admin, dto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.menuCategoryName()).isEqualTo("테스트 카테고리");
        System.out.println("✅ 생성 성공: " + response.menuCategoryName());
    }

    @DisplayName("전체 카테고리를 조회할 수 있다")
    @Test
    void getAllMenuCategoriesTest() {
        // given
        User admin = org.mockito.Mockito.mock(User.class);
        org.mockito.Mockito.when(admin.getRole()).thenReturn(UserRole.MASTER);

        // when
        List<MenuCategoryResponseDto> categories = menuCategoryService.getAllMenuCategories();

        // then
        assertThat(categories).isNotNull();
        assertThat(categories.size()).isGreaterThanOrEqualTo(0); // 최소 0개라도 존재
        System.out.println("✅ 조회 성공: 총 " + categories.size() + "개의 카테고리");
    }

    @Test
    @DisplayName("관리자가 특정 ID의 카테고리를 조회할 수 있다")
    void getMenuCategoryByIdTest() {
        // given
        User admin = Mockito.mock(User.class);
        Mockito.when(admin.getRole()).thenReturn(UserRole.MASTER);

        MenuCategoryRequestDto dto = new MenuCategoryRequestDto("단일 조회 카테고리");
        MenuCategoryResponseDto created = menuCategoryService.createMenuCategory(admin, dto);

        // when
        MenuCategoryResponseDto found = menuCategoryService.getMenuCategoryById(created.id());

        // then
        assertThat(found).isNotNull();
        assertThat(found.menuCategoryName()).isEqualTo("단일 조회 카테고리");
        System.out.println("✅ 단일 조회 성공: " + found.menuCategoryName());
    }

    @DisplayName("관리자가 카테고리를 수정할 수 있다")
    @Test
    void updateMenuCategoryTest() {
        // given
        User admin = org.mockito.Mockito.mock(User.class);
        org.mockito.Mockito.when(admin.getRole()).thenReturn(UserRole.MASTER);

        // 기존 카테고리 생성
        MenuCategoryRequestDto createDto = new MenuCategoryRequestDto("초기 카테고리");
        MenuCategoryResponseDto created = menuCategoryService.createMenuCategory(admin, createDto);

        // 수정할 DTO
        MenuCategoryRequestDto updateDto = new MenuCategoryRequestDto("수정된 카테고리");

        // when
        MenuCategoryResponseDto updated = menuCategoryService.updateMenuCategory(admin, created.id(), updateDto);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.menuCategoryName()).isEqualTo("수정된 카테고리");
        System.out.println("✅ 수정 성공: " + updated.menuCategoryName());
    }

    @DisplayName("관리자가 카테고리를 삭제할 수 있다")
    @Test
    void deleteMenuCategoryTest() {
        // given
        User admin = Mockito.mock(User.class);
        Mockito.when(admin.getRole()).thenReturn(UserRole.MASTER);

        MenuCategoryRequestDto dto = new MenuCategoryRequestDto("삭제용 카테고리");
        MenuCategoryResponseDto created = menuCategoryService.createMenuCategory(admin, dto);

        // when
        menuCategoryService.deleteMenuCategory(admin, created.id());

        // then
        List<MenuCategoryResponseDto> categories = menuCategoryService.getAllMenuCategories();
        boolean exists = categories.stream()
                .anyMatch(c -> c.id().equals(created.id()));

        assertThat(exists).isFalse();
        System.out.println("✅ 삭제 성공: ID = " + created.id());
    }
}