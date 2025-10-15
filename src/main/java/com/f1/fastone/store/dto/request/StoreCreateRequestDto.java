package com.f1.fastone.store.dto.request;

import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalTime;

public record StoreCreateRequestDto(
        @NotBlank(message = "가게 이름은 필수입니다.")
        @Size(max = 120, message = "가게 이름은 120자를 초과할 수 없습니다.")
        String name,

        @Length(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
        String phone,

        @Length(max = 10, message = "우편번호는 10자를 초과할 수 없습니다.")
        String postalCode,

        @Length(max = 120, message = "도시명은 120자를 초과할 수 없습니다.")
        String city,

        String address,
        String addressDetail,

        BigDecimal latitude,
        BigDecimal longitude,

        LocalTime openTime,
        LocalTime closeTime,

        Long categoryId
) {
    public Store toEntity(User owner, StoreCategory category) {
        return Store.builder()
                .name(this.name)
                .phone(this.phone)
                .postalCode(this.postalCode)
                .city(this.city)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .openTime(this.openTime)
                .closeTime(this.closeTime)
                .owner(owner)
                .category(category)
                .build();
    }
}
