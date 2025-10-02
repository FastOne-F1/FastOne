package com.f1.fastone.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressCreateRequestDto {

    @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
    private String postalCode;

    @NotBlank(message = "시/도는 필수입니다")
    @Size(max = 120, message = "시/도는 120자 이하여야 합니다")
    private String city;

    @NotBlank(message = "기본 주소는 필수입니다")
    private String address;

    private String addressDetail;  // 상세주소는 선택사항
}
