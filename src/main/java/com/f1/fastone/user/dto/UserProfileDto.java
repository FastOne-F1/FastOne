package com.f1.fastone.user.dto;

import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    // 기본 사용자 정보
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;
    private Boolean isPublic;
    private UserRole role;

    // 주소 목록 (여러 개)
    private List<UserAddressDto> addresses;

    // User 엔티티에서 DTO 변환
    public static UserProfileDto from(User user){
        List<UserAddressDto> addressDtos = user.getAddresses().stream()
                .map(UserAddressDto::from)
                .collect(Collectors.toList());

        return UserProfileDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isPublic(user.getIsPublic())
                .role(user.getRole())
                .addresses(addressDtos != null ? addressDtos : new ArrayList<>())
                .build();
    }

    

}
