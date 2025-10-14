package com.f1.fastone.order.dto.request;

import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderRequestDto {

    String username;
    UUID storeId;
    UUID cartId;
    String requestNote;

    ShipToDto shipTo;

    PaymentDto payment;

    public Order toEntity(User user, Store store) {
        ShipToDto shipTo = this.getShipTo();
        return Order.builder()
                .user(user)
                .store(store)
//                .orderItems(orderItems)
                .totalPrice(this.getPayment().getTotalPrice())
                .requestNote(this.getRequestNote())
                .shipToName(shipTo.getName())
                .shipToPhone(shipTo.getPhone())
                .postalCode(shipTo.getPostalCode())
                .city(shipTo.getCity())
                .address(shipTo.getAddress())
                .addressDetail(shipTo.getAddressDetail())
                .build();
    }


}
