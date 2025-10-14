package com.f1.fastone.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ItemCreateRequestDto(
        @NotNull UUID menuId,
        @Min(1) int quantity
) {}
