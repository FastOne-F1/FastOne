package com.f1.fastone.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheKey {

	REVIEW_SUMMARY("review:summary:", 3600);

	private final String prefix;
	private final long ttlSeconds;

	public String key(Object id) {
		return prefix + id;
	}
}