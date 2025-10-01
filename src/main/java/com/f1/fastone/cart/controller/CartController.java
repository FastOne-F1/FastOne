package com.f1.fastone.cart.controller;

import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/store/{storeId}")
    public ApiResponse initCart(@AuthenticationPrincipal UserDetails user,
                                @PathVariable String storeId) {
        cartService.initCart(user.getUsername(), storeId);
        return ApiResponse.created();
    }
}