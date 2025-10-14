package com.f1.fastone.payment.entity;

public enum PaymentStatus {
    READY,      // 결제 요청 전
    PENDING,    // 결제 진행 중
    SUCCESS,    // 결제 성공
    FAIL,       // 결제 실패
    CANCEL      // 결제 취소
}