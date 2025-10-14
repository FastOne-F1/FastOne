package com.f1.fastone.payment.controller;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.payment.dto.request.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping
    public ApiResponse<Void> createPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.createPayment(userDetails.getUsername(), paymentRequestDto);
        return ApiResponse.created();
    }
}
