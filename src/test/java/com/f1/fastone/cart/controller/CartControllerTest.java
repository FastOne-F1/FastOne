package com.f1.fastone.cart.controller;

import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.request.ItemUpdateRequestDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.util.TestMenuFactory;
import com.f1.fastone.util.TestStoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.aop.auto=false"
})
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    private UUID storeId;
    private UUID menuId;
    private Menu menu;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        CartService cartService() {
            return Mockito.mock(CartService.class);
        }
    }

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();
        Store store = TestStoreFactory.createStore(user, StoreCategory.of("패스트푸드"));
        menu = TestMenuFactory.createMenu(store);
        storeId = store.getId();
        menuId = menu.getId();
    }

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공")
    @WithMockUser(username = "user1")
    void addItem_Success() throws Exception {
        ItemCreateRequestDto request = new ItemCreateRequestDto(menuId, 2);
        ItemCreateResponseDto response = ItemCreateResponseDto.from(menu, 2, LocalDateTime.now());

        Mockito.when(cartService.addItem(eq("user1"), eq(storeId), any(ItemCreateRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/carts/store/{storeId}/items", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuName").value(menu.getName()))
                .andExpect(jsonPath("$.data.price").value(menu.getPrice()))
                .andExpect(jsonPath("$.data.quantity").value(2));
    }

    @Test
    @DisplayName("장바구니 수량 변경 성공")
    @WithMockUser(username = "user1")
    void updateQuantity_Success() throws Exception {
        ItemUpdateRequestDto request = new ItemUpdateRequestDto(3);

        mockMvc.perform(patch("/carts/store/{storeId}/items/{menuId}", storeId, menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 메뉴 삭제 성공")
    @WithMockUser(username = "user1")
    void removeItem_Success() throws Exception {
        mockMvc.perform(delete("/carts/store/{storeId}/items/{menuId}", storeId, menuId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 전체 삭제 성공")
    @WithMockUser(username = "user1")
    void clearCart_Success() throws Exception {
        mockMvc.perform(delete("/carts/store/{storeId}", storeId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 전체 조회 성공")
    @WithMockUser(username = "user1")
    void getCart_Success() throws Exception {
        CartResponseDto response = new CartResponseDto("storeId", "테스트 가게", List.of());
        Mockito.when(cartService.getCart("user1")).thenReturn(List.of(response));

        mockMvc.perform(get("/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].storeName").value("테스트 가게"));
    }
}
