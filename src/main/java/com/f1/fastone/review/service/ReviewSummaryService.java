package com.f1.fastone.review.service;

import com.f1.fastone.ai.service.AiService;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.user.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.f1.fastone.common.config.CacheKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewSummaryService {

	private final AiService aiService;
	private final StringRedisTemplate stringRedisTemplate;

	public String summarizeRecentReviews(User user, UUID storeId, List<Review> reviews) {
		CacheKey keyType = CacheKey.REVIEW_SUMMARY;
		String cacheKey = keyType.key(storeId.toString());

		if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(cacheKey))) {
			return stringRedisTemplate.opsForValue().get(cacheKey);
		}

		String combinedReviews = reviews.stream()
			.map(Review::getContent)
			.collect(Collectors.joining("\n\n"));

		String prompt = """
			너는 리뷰 내용을 분석하고 핵심 내용을 한 문단으로 요약하는 AI야.
			다음은 최근 10개의 리뷰야. 공통적인 인상, 자주 언급된 장단점 등을 중심으로 자연스럽게 요약해줘.

			리뷰 목록:
			%s
			""".formatted(combinedReviews);

		String summary = aiService.generateDescription(user, prompt);

		stringRedisTemplate.opsForValue().set(
			cacheKey,
			summary,
			keyType.getTtlSeconds(),
			TimeUnit.SECONDS
		);

		return summary;
	}

	public String getCachedSummary(UUID storeId) {
		return stringRedisTemplate.opsForValue()
			.get(CacheKey.REVIEW_SUMMARY.key(storeId.toString()));
	}
}