package com.f1.fastone.order.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.order.dto.request.OrderRequestDto;
import com.f1.fastone.order.dto.request.OrderStatusRequestDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public ApiResponse<OrderResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto response = orderService.createOrder(userDetails.getUsername(), requestDto);
        return ApiResponse.created(response);
    }




    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponseDto> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable UUID orderId, @RequestBody OrderStatusRequestDto requestDto) {
        OrderResponseDto response = orderService.updateOrderStatus(orderId, requestDto);
        return ApiResponse.success(response);
    }



}
