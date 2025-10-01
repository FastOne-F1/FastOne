package com.f1.fastone.util;

import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.entity.StoreRating;
import com.f1.fastone.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public class TestStoreFactory {

	public static Store createStore(User owner, StoreCategory category) {
		Store store = Store.builder()
			.id(UUID.randomUUID())
			.name("테스트 가게")
			.phone("010-1234-5678")
			.postalCode("12345")
			.city("Seoul")
			.address("강남구 역삼동")
			.addressDetail("101호")
			.latitude(BigDecimal.valueOf(37.5665))
			.longitude(BigDecimal.valueOf(126.9780))
			.openTime(LocalTime.of(9, 0))
			.closeTime(LocalTime.of(22, 0))
			.owner(owner)
			.category(category)
			.build();

		StoreRating rating = StoreRating.builder()
			.store(store)
			.reviewCount(0)
			.scoreAvg(BigDecimal.ZERO)
			.build();

		store.addStoreRating(rating);
		return store;
	}
}
