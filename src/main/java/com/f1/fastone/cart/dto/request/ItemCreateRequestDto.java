package com.f1.fastone.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ItemCreateRequestDto(
        @NotBlank UUID menuId,
        @NotNull @Min(1) Integer quantity
) {}
