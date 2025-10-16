package com.f1.fastone.review.service;

import com.f1.fastone.ai.service.AiService;
import com.f1.fastone.user.entity.User;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.f1.fastone.common.config.CacheKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewSummaryService {

	private final AiService aiService;
	private final StringRedisTemplate stringRedisTemplate;

	public String summarizeReview(User user, String reviewId, String content) {
		CacheKey keyType = CacheKey.REVIEW_SUMMARY;
		String cacheKey = keyType.key(reviewId);

		if (stringRedisTemplate.hasKey(cacheKey)) {
			return stringRedisTemplate.opsForValue().get(cacheKey);
		}

		String prompt = """
            너는 리뷰 내용을 간결하게 요약하는 AI야.
            다음 리뷰를 한줄로 자연스럽게 요약해줘.

            리뷰 내용:
            %s
            """.formatted(content);

		String summary = aiService.generateDescription(user, prompt);

		stringRedisTemplate.opsForValue().set(
				cacheKey,
				summary,
				keyType.getTtlSeconds(),
				TimeUnit.SECONDS
		);
		return summary;
	}

	public String getCachedSummary(String reviewId) {
		return stringRedisTemplate.opsForValue()
			.get(CacheKey.REVIEW_SUMMARY.key(reviewId));
	}
}