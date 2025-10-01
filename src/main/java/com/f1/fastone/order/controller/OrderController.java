package com.f1.fastone.order.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.order.dto.request.OrderCreateRequestDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping("")
    public ApiResponse<OrderResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody OrderCreateRequestDto requestDto) {
        OrderResponseDto response = orderService.createOrder(userDetails.getUsername(), requestDto);
        return ApiResponse.created(response);
    }

}
