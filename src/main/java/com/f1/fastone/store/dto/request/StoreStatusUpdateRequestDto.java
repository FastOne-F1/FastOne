package com.f1.fastone.store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreStatusUpdateRequestDto {
    @NotNull(message = "영업 상태는 필수입니다.")
    private Boolean isOpen;
}
