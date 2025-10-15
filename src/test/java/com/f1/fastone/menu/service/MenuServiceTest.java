package com.f1.fastone.menu.service;

import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.menu.dto.request.MenuUpdateRequestDto;
import com.f1.fastone.menu.dto.response.MenuResponseDto;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuCategoryRepository;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private StoreRepository storeRepository;

    private Store createTestStore(UUID storeId) throws Exception {
        Constructor<Store> constructor = Store.class.getDeclaredConstructor();
        constructor.setAccessible(true); // protected 열기
        Store store = constructor.newInstance();
        ReflectionTestUtils.setField(store, "id", storeId);
        ReflectionTestUtils.setField(store, "name", "테스트 가게");
        return store;
    }

    // CreateTest
    @Test
    @DisplayName("메뉴 생성 성공")
    void createMenu_success() throws Exception{
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = createTestStore(storeId);

        Menu menu = new Menu("치킨", "맛있는 치킨", 18000, false, "imageUrl", store, null);
        ReflectionTestUtils.setField(menu, "id", menuId);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

        // when
        ApiResponse<MenuResponseDto> response = menuService.getMenu(menuId);

        // then
        assertThat(response.data().id()).isEqualTo(menuId);
    }

    @Test
    @DisplayName("특정 가게의 메뉴 전체 조회 성공")
    void getMenusByStore_success() throws Exception{
        // given
        UUID storeId = UUID.randomUUID();
        Store store = createTestStore(storeId);

        Menu menu1 = new Menu("치킨", "맛있는 치킨", 18000, false, "imageUrl", store, null);
        ReflectionTestUtils.setField(menu1, "id", UUID.randomUUID());

        Menu menu2 = new Menu("피자", "맛있는 피자", 22000, false, "imageUrl2", store, null);
        ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());

        given(menuRepository.findByStoreId(storeId)).willReturn(List.of(menu1, menu2));

        // when
        ApiResponse<List<MenuResponseDto>> response = menuService.getMenusByStore(storeId);

        // then
        assertThat(response.data()).hasSize(2);
    }

    @Test
    @DisplayName("메뉴 수정 성공")
    void updateMenu_success() throws Exception{
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = createTestStore(storeId);

        Menu menu = new Menu("치킨", "맛있는 치킨", 18000, false, "imageUrl", store, null);
        ReflectionTestUtils.setField(menu, "id", menuId);

        MenuUpdateRequestDto updateDto = new MenuUpdateRequestDto(
                "양념치킨", "매운 양념치킨", 19000, false, "newImageUrl", null
        );

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        // when
        ApiResponse<MenuResponseDto> response = menuService.updateMenu(menuId, updateDto);

        // then
        assertThat(response.data().name()).isEqualTo("양념치킨");
        assertThat(response.data().price()).isEqualTo(19000);
    }

    @Test
    @DisplayName("메뉴 품절 여부 수정 성공")
    void updateSoldOutStatus_success() throws Exception {
        //given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = createTestStore(storeId);

        //기존 메뉴 : 품절 상태 false
        Menu menu = new Menu("치킨", "맛있는 치킨", 18000, false, "imageUrl", store, null);
        ReflectionTestUtils.setField(menu, "id", menuId);

        //findById() 가 메뉴를 반환하도록 Mock 설정
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        //when - soldOut = true로 변경
        ApiResponse<MenuResponseDto> response = menuService.updateSoldOutStatus(menuId, true);

        // then
        assertThat(response.data().soldOut()).isTrue(); // 풀절 여부가 true로 수정됐는지
        assertThat(response.data().name()).isEqualTo("치킨");

    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void deleteMenu_success() throws Exception{
        // given
        UUID menuId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Store store = createTestStore(storeId);

        Menu menu = new Menu("치킨", "맛있는 치킨", 18000, false, "imageUrl", store, null);
        ReflectionTestUtils.setField(menu, "id", menuId);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

        // when
        ApiResponse<MenuResponseDto> response = menuService.deleteMenu(menuId);

        // then
        assertThat(response.data().id()).isEqualTo(menuId);
    }
}
