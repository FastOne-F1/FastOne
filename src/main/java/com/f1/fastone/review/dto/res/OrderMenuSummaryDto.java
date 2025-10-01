package com.f1.fastone.review.dto.res;

import java.util.UUID;

public record OrderMenuSummaryDto(

	UUID menuId,

	String menuName,

	int price,

	int quantity,

	String categoryName

) {}