package com.f1.fastone.review.service;

import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.f1.fastone.common.config.CacheKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewSummaryService {

	private final ChatClient chatClient;
	private final StringRedisTemplate stringRedisTemplate;

	@Async
	public void summarizeReviewAsync(String reviewId, String content) {
		CacheKey keyType = CacheKey.REVIEW_SUMMARY;
		String cacheKey = keyType.key(reviewId);

		if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(cacheKey))) {
			return;
		}

		String prompt = """
            너는 리뷰 내용을 간결하게 요약하는 AI야.
            다음 리뷰를 2~3줄로 자연스럽게 요약해줘.

            리뷰 내용:
            %s
            """.formatted(content);

		try {
			String summary = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

			stringRedisTemplate.opsForValue().set(
				cacheKey,
				summary,
				keyType.getTtlSeconds(),
				TimeUnit.SECONDS
			);

		} catch (Exception ignored) {
		}
	}

	public String getCachedSummary(String reviewId) {
		return stringRedisTemplate.opsForValue()
			.get(CacheKey.REVIEW_SUMMARY.key(reviewId));
	}
}