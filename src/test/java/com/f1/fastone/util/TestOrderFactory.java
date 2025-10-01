package com.f1.fastone.util;

import java.util.UUID;

import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderStatus;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;

public class TestOrderFactory {

	public static Order createOrder(User user, Store store) {
		return Order.builder()
			.id(UUID.randomUUID())
			.status(OrderStatus.CREATED)
			.totalPrice(20000)
			.requestNote("빨리 배달해주세요")
			.shipToName("테스트 유저")
			.shipToPhone("010-9999-8888")
			.postalCode("12345")
			.city("Seoul")
			.address("강남구 테헤란로")
			.addressDetail("202호")
			.user(user)
			.store(store)
			.build();
	}
}