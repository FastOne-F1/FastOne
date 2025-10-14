package com.f1.fastone.user.dto;

import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateResponseDto {

    // 기본 사용자 정보
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;
    private Boolean isPublic;
    private UserRole role;

    // 주소 정보
    private String postalCode;
    private String city;
    private String address;
    private String addressDetail;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // 메타 정보 (업데이트된 시간 등)
    private String updatedAt;       // 수정 시간
    private String message;         // 업데이트 완료 메시지

    // User 엔티티에서 ResponseDto로 변환하는 정적 메서드
    public static UserProfileUpdateResponseDto from(User user) {
        var builder = UserProfileUpdateResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isPublic(user.getIsPublic())
                .role(user.getRole())
                .updatedAt(user.getUpdatedAt().toString())
                .message("프로필이 성공적으로 업데이트되었습니다.");

        if (!user.getAddresses().isEmpty()) {
            var firstAddress = user.getAddresses().get(0);
            builder.postalCode(firstAddress.getPostalCode())
                    .city(firstAddress.getCity())
                    .address(firstAddress.getAddress())
                    .addressDetail(firstAddress.getAddressDetail());
        }

        return builder.build();
    }
}
