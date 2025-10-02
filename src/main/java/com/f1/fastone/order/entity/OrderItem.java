package com.f1.fastone.order.entity;

import com.f1.fastone.cart.entity.CartItem;
import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.order.dto.OrderItemDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_order_item")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String menuName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public void setOrder(Order order) {
        this.order = order;

        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }




}