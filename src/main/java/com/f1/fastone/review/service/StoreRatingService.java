package com.f1.fastone.review.service;

import org.springframework.stereotype.Service;

import com.f1.fastone.store.entity.Store;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreRatingService {


	public void increaseRating(Store store, int newScore) {
		store.getStoreRating().addScore(newScore);
	}

	public void updateRating(Store store, int oldScore, int newScore) {
		store.getStoreRating().updateScore(oldScore, newScore);
	}

	public void decreaseRating(Store store, int oldScore) {
		store.getStoreRating().removeScore(oldScore);
	}
}