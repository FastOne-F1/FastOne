package com.f1.fastone.cart.controller;

import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.request.ItemUpdateRequestDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/store/{storeId}/items")
    public ApiResponse<ItemCreateResponseDto> addItem(@AuthenticationPrincipal UserDetailsImpl user,
                                                      @PathVariable UUID storeId,
                                                      @RequestBody ItemCreateRequestDto requestDto) {
        return ApiResponse.success(cartService.addItem(user.getUsername(), storeId, requestDto));
    }

    @PatchMapping("/store/{storeId}/items/{menuId}")
    public ApiResponse<Void> updateQuantity(@AuthenticationPrincipal UserDetailsImpl user,
                                            @PathVariable UUID storeId,
                                            @PathVariable String menuId,
                                            @RequestBody ItemUpdateRequestDto request) {
        cartService.setQuantity(user.getUsername(), storeId, menuId, request.quantity());
        return ApiResponse.success();
    }

    @DeleteMapping("/store/{storeId}/items/{menuId}")
    public ApiResponse<Void> removeItem(@AuthenticationPrincipal UserDetailsImpl user,
                                        @PathVariable UUID storeId,
                                        @PathVariable String menuId) {
        cartService.removeItem(user.getUsername(), storeId, menuId);
        return ApiResponse.success();
    }

    @DeleteMapping("/store/{storeId}")
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl user,
                                       @PathVariable UUID storeId) {
        cartService.clearCart(user.getUsername(), storeId);
        return ApiResponse.success();
    }

    @GetMapping()
    public ApiResponse<List<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ApiResponse.success(cartService.getCart(user.getUsername()));
    }
}