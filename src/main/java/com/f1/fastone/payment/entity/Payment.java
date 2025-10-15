package com.f1.fastone.payment.entity;

import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.payment.dto.request.PaymentConfirmRequestDto;
import com.f1.fastone.payment.dto.request.PaymentRequestDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.order.entity.Order;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String paymentKey; // 외부 결제 고유 키

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String method; // 카드, 계좌이체 등

    private LocalDateTime approvedAt; // 결제 완료 시각 (외부 결제사에서 내려주는 값)

    @Column(columnDefinition = "TEXT")
    private String cartSnapshot;

    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Payment(long amount, String method, PaymentStatus status, String cartSnapshot, Long addressId, User user, Store store) {
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.cartSnapshot = cartSnapshot;
        this.addressId = addressId;
        this.user = user;
        this.store = store;
    }

    public static Payment create(PaymentRequestDto request, String cartSnapshot, Long addressId, User user, Store store) {
        return Payment.builder()
                .amount(request.amount())
                .method(request.method())
                .status(PaymentStatus.READY)
                .cartSnapshot(cartSnapshot)
                .addressId(addressId)
                .user(user)
                .store(store)
                .build();
    }

    public void markSuccess(PaymentConfirmRequestDto request) {
        this.paymentKey = request.paymentKey();
        this.status = PaymentStatus.SUCCESS;
        this.approvedAt = request.approvedAt();
    }

    public void markFail() {
        this.status = PaymentStatus.FAIL;
    }

    public void markCancel() {
        this.status = PaymentStatus.CANCEL;
    }
}