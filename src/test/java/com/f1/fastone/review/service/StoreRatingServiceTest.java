package com.f1.fastone.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.store.entity.StoreRating;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;

class StoreRatingServiceTest {

	private StoreRatingService storeRatingService;
	private Store testStore;

	@BeforeEach
	void setUp() {
		storeRatingService = new StoreRatingService();

		User owner = User.builder()
			.username("owner")
			.email("owner@test.com")
			.password("encoded")
			.role(UserRole.OWNER)
			.build();

		StoreCategory category = StoreCategory.of("한식");

		testStore = Store.builder()
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
			.store(testStore)
			.reviewCount(0)
			.scoreAvg(BigDecimal.ZERO)
			.build();

		testStore.addStoreRating(rating);
	}

	@Test
	@DisplayName("리뷰 추가 시 평균과 개수 증가")
	void increaseRating() {
		storeRatingService.increaseRating(testStore, 5);

		assertThat(testStore.getStoreRating().getReviewCount()).isEqualTo(1);
		assertThat(testStore.getStoreRating().getScoreAvg())
			.isEqualTo(BigDecimal.valueOf(5.0).setScale(1, RoundingMode.HALF_UP));	}

	@Test
	@DisplayName("리뷰 수정 시 평균 업데이트")
	void updateRating() {
		storeRatingService.increaseRating(testStore, 5);
		storeRatingService.updateRating(testStore, 5, 3);

		assertThat(testStore.getStoreRating().getReviewCount()).isEqualTo(1);
		assertThat(testStore.getStoreRating().getScoreAvg())
			.isEqualTo(BigDecimal.valueOf(3.0).setScale(1, RoundingMode.HALF_UP));	}

	@Test
	@DisplayName("리뷰 삭제 시 평균과 개수 감소")
	void decreaseRating() {
		storeRatingService.increaseRating(testStore, 5);
		storeRatingService.increaseRating(testStore, 3);
		storeRatingService.decreaseRating(testStore, 5);

		assertThat(testStore.getStoreRating().getReviewCount()).isEqualTo(1);
		assertThat(testStore.getStoreRating().getScoreAvg())
			.isEqualTo(BigDecimal.valueOf(3.0).setScale(1, RoundingMode.HALF_UP));	}

	@Test
	@DisplayName("마지막 리뷰 삭제 시 초기화")
	void decreaseRating_toZero() {
		storeRatingService.increaseRating(testStore, 4);

		storeRatingService.decreaseRating(testStore, 4);

		assertThat(testStore.getStoreRating().getReviewCount()).isZero();
		assertThat(testStore.getStoreRating().getScoreAvg()).isEqualTo(BigDecimal.ZERO);
	}
}