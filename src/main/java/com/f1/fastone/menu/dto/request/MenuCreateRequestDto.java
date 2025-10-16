// MenuCreateRequestDto.java
package com.f1.fastone.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MenuCreateRequestDto(
        @NotBlank String name,
        String description,
        @NotNull Integer price,
        boolean soldOut,
        String imageUrl,
        @NotNull UUID storeId,
        UUID categoryId,
        boolean useAi
) {}
