package com.f1.fastone.payment.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.payment.dto.request.PaymentRequestDto;
import com.f1.fastone.payment.dto.response.PaymentPrepareResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.f1.fastone.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/prepare/{storeId}")
    public ApiResponse<PaymentPrepareResponseDto> preparePayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId) {
        return ApiResponse.success(paymentService.preparePayment(userDetails.getUsername(), storeId));
    }

    @PostMapping
    public ApiResponse<Void> createPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.createPayment(userDetails.getUsername(), paymentRequestDto);
        return ApiResponse.created();
    }
}
