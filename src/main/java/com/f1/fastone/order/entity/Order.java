package com.f1.fastone.order.entity;

import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.payment.entity.Payment;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(length = 200)
    private String requestNote;

    @Column(length = 50)
    private String shipToName;
    @Column(length = 20)
    private String shipToPhone;

    @Column(length = 10)
    private String postalCode;
    @Column(length = 120)
    private String city;
    @Column
    private String address;
    @Column
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Review review;

    public static Order create(User user, Payment payment, UserAddress userAddress) {
        return Order.builder()
                .user(user)
                .store(payment.getStore())
                .shipToName(user.getNickname())
                .shipToPhone(user.getPhoneNumber())
                .postalCode(userAddress.getPostalCode())
                .city(userAddress.getCity())
                .address(userAddress.getAddress())
                .addressDetail(userAddress.getAddressDetail())
                .totalPrice(Math.toIntExact(payment.getAmount()))
                .status(OrderStatus.CREATED)
                .build();
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }



}