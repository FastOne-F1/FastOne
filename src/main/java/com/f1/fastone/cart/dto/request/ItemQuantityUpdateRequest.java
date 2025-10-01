package com.f1.fastone.cart.dto.request;

import jakarta.validation.constraints.Min;

public record ItemQuantityUpdateRequest(
    @Min(1)
    int quantity
) {}
