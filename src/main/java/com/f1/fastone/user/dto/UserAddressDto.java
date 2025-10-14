package com.f1.fastone.user.dto;

import com.f1.fastone.user.entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDto {

    private Long id;
    private String postalCode;      // 우편번호
    private String city;            // 시/도 + 구/군
    private String address;         // 기본주소
    private String addressDetail;   // 상세주소

    // UserAddress 엔티티에서 DTO로 변환
    public static UserAddressDto from(UserAddress userAddress) {
        return UserAddressDto.builder()
                .id(userAddress.getId())
                .postalCode(userAddress.getPostalCode())
                .city(userAddress.getCity())
                .address(userAddress.getAddress())
                .addressDetail(userAddress.getAddressDetail())
                .build();
    }
}
