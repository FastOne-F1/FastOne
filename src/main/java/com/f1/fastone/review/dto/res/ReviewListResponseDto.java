package com.f1.fastone.review.dto.res;

import java.util.List;

public record ReviewListResponseDto(

	List<ReviewResponseDto> reviews,

	long totalCount,

	int page,

	int size

) {}