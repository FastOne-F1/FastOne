package com.f1.fastone.cart.service;

import com.f1.fastone.cart.dto.CartRedisItem;
import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.util.TestMenuFactory;
import com.f1.fastone.util.TestStoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private CartService cartService;
    private Store store;
    private Menu menu;
    private UUID storeId;
    private UUID menuId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();
        store = TestStoreFactory.createStore(user, StoreCategory.of("패스트푸드"));
        menu = TestMenuFactory.createMenu(store);
        storeId = store.getId();
        menuId = menu.getId();
    }

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공")
    void addItem_Success() {
        ItemCreateRequestDto request = new ItemCreateRequestDto(menuId, 2);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findByIdAndStoreId(menuId, storeId)).thenReturn(Optional.of(menu));

        cartService.addItem("user1", storeId, request);

        verify(cartRepository, times(1)).addMenu(eq("user1"), eq(store), eq(menuId.toString()), any(CartRedisItem.class));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 상점 없음")
    void addItem_StoreNotFound() {
        ItemCreateRequestDto request = new ItemCreateRequestDto(menuId, 2);
        UUID invalidStoreId = UUID.randomUUID();
        when(storeRepository.findById(invalidStoreId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cartService.addItem("user1", invalidStoreId, request));
    }

    @Test
    @DisplayName("장바구니 추가 실패 - 메뉴 없음")
    void addItem_MenuNotFound() {
        ItemCreateRequestDto request = new ItemCreateRequestDto(UUID.randomUUID(), 2);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findByIdAndStoreId(any(UUID.class), eq(storeId))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cartService.addItem("user1", storeId, request));
    }

    @Test
    @DisplayName("장바구니 수량 변경 성공")
    void setQuantity_Success() {
        cartService.setQuantity("user1", storeId, menuId.toString(), 3);
        verify(cartRepository, times(1)).updateQuantity("user1", storeId.toString(), menuId.toString(), 3);
    }

    @Test
    @DisplayName("장바구니 메뉴 삭제 성공")
    void removeItem_Success() {
        cartService.removeItem("user1", storeId, menuId.toString());
        verify(cartRepository, times(1)).removeMenu("user1", storeId.toString(), menuId.toString());
    }

    @Test
    @DisplayName("장바구니 전체 삭제 성공")
    void clearCart_Success() {
        cartService.clearCart("user1", storeId);
        verify(cartRepository, times(1)).clearCart("user1", storeId.toString());
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCart_Success() {
        CartResponseDto response = new CartResponseDto(storeId.toString(), "테스트 가게", List.of());
        when(cartRepository.findAllByUser("user1")).thenReturn(List.of(response));

        List<CartResponseDto> result = cartService.getCart("user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).storeName()).isEqualTo("테스트 가게");
        verify(cartRepository, times(1)).findAllByUser("user1");
    }
}
