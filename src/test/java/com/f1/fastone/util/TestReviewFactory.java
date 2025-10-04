package com.f1.fastone.util;

import com.f1.fastone.review.entity.Review;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.user.entity.User;

import java.util.UUID;

public class TestReviewFactory {

	public static Review createReview(User user, Store store, Order order, int score, String content) {
		return Review.builder()
			.id(UUID.randomUUID())
			.score(score)
			.content(content)
			.user(user)
			.store(store)
			.order(order)
			.build();
	}

	public static Review createWithReply(User user, Store store, Order order, int score, String content, String reply) {
		Review review = createReview(user, store, order, score, content);
		review.addReply(reply);
		return review;
	}

	public static Review createDeleted(User user, Store store, Order order, int score, String content, String deletedBy) {
		Review review = createReview(user, store, order, score, content);
		review.softDelete(deletedBy);
		return review;
	}
}