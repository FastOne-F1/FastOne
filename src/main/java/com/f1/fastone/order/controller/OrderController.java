package com.f1.fastone.order.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.order.dto.request.OrderSearchRequestDto;
import com.f1.fastone.order.dto.request.OrderStatusRequestDto;
import com.f1.fastone.order.dto.response.OrderDetailResponseDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("")
    public ApiResponse<List<OrderResponseDto>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        List<OrderResponseDto> response = orderService.getOrders(username);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponseDto> getOrderDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("orderId") UUID orderId) {
        String username = userDetails.getUsername();
        OrderDetailResponseDto response = orderService.getOrderDetail(username, orderId);
        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    @Operation(summary = "주문 검색 (고객용)")
    public ApiResponse<OrderResponseDto> searchOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @Parameter(description = "가게 상호명 / 메뉴명 / 지역")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (10, 30, 50)")
            @RequestParam(defaultValue = "10") String size, // 한 페이지 당
            @Parameter(description = "생성일 기준 정렬")
            @RequestParam(defaultValue = "desc") String sortDirection // desc, asc
    ) {
        OrderSearchRequestDto searchRequest = OrderSearchRequestDto.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .sortDirection(sortDirection)
                .build();
        List<OrderResponseDto> response = orderService.searchOrders(userDetails.getUsername(), searchRequest);
        return null;
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponseDto> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable("orderId") UUID orderId, @RequestBody OrderStatusRequestDto requestDto) {
        OrderResponseDto response = orderService.updateOrderStatus(userDetails, orderId, requestDto);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("orderId") UUID orderId) {
        orderService.deleteOrder(userDetails, orderId);
        return ApiResponse.success();

    }


}
