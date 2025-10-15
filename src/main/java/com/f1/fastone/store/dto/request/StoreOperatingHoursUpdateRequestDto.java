package com.f1.fastone.store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreOperatingHoursUpdateRequestDto {
    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;
    
    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closeTime;
}
