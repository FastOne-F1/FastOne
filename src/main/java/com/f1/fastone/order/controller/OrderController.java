package com.f1.fastone.order.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.dto.PageResponse;
import com.f1.fastone.common.util.PageUtils;
import com.f1.fastone.order.dto.request.OrderStatusRequestDto;
import com.f1.fastone.order.dto.response.OrderDetailResponseDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    @Operation(summary = "주문 내역 조회")
    public ApiResponse<List<OrderResponseDto>> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String username = userDetails.getUsername();
        List<OrderResponseDto> response = orderService.getOrders(username);
        return ApiResponse.success(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회")
    public ApiResponse<OrderDetailResponseDto> getOrderDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("orderId") UUID orderId) {
        String username = userDetails.getUsername();
        OrderDetailResponseDto response = orderService.getOrderDetail(username, orderId);
        return ApiResponse.success(response);
    }

//    @GetMapping("/search")
//    @Operation(summary = "주문 내역 검색")
//    public ApiResponse<List<OrderResponseDto>> searchOrders(
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
//
//            @Parameter(description = "가게 상호명 / 메뉴명 / 고객명(관리자용) 등")
//            @RequestParam(required = false) String search,
//
//            @Parameter(description = "페이지 번호")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "페이지 크기 (10, 30, 50)")
//            @RequestParam(defaultValue = "10") int size // 한 페이지 당
//    ) {
////        Pageable pageable = PageUtils.createdAtDesc(page, size);
////        PageResponse<OrderResponseDto> response = orderService.searchOrders(userDetails, pageable, keyword);
//            List<OrderResponseDto> response = orderService.searchOrders(userDetails, search);
//        return ApiResponse.success(response);
//    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경")
    public ApiResponse<OrderResponseDto> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable("orderId") UUID orderId, @RequestBody OrderStatusRequestDto requestDto) {
        OrderResponseDto response = orderService.updateOrderStatus(userDetails, orderId, requestDto);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소")
    public ApiResponse<Void> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("orderId") UUID orderId) {
        orderService.deleteOrder(userDetails, orderId);
        return ApiResponse.success();

    }


}
