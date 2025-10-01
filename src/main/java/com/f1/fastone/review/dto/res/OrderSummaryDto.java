package com.f1.fastone.review.dto.res;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryDto(

	LocalDateTime orderDate,

	int totalPrice,

	List<OrderMenuSummaryDto> menus

) {}