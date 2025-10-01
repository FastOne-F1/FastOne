package com.f1.fastone.cart.controller;

import com.f1.fastone.cart.dto.CartItemDto;
import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.dto.ApiResponse;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/store/{storeId}")
    public ApiResponse<?> initCart(@AuthenticationPrincipal UserDetails user,
                                @PathVariable UUID storeId) {
        cartService.initCart(user.getUsername(), storeId);
        return ApiResponse.created();
    }

    @PostMapping("/store/{storeId}/items")
    public ApiResponse<ItemCreateResponseDto> addItem(@AuthenticationPrincipal UserDetails user,
                                                      @PathVariable UUID storeId,
                                                      @RequestBody ItemCreateRequestDto requestDto) {

        return ApiResponse.success(cartService.addItem(user.getUsername(), storeId, requestDto));
    }
}