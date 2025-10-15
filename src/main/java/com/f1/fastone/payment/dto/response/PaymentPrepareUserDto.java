package com.f1.fastone.payment.dto.response;

import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;

public record PaymentPrepareUserDto(
        String nickname,
        String phoneNumber,
        String address
) {
    public static PaymentPrepareUserDto from(User user, UserAddress defaultAddress) {
        String fullAddress = String.format("(%s) %s %s %s", defaultAddress.getPostalCode(), defaultAddress.getCity(), defaultAddress.getAddress(), defaultAddress.getAddressDetail());
        return new PaymentPrepareUserDto(user.getNickname(), user.getPhoneNumber(), fullAddress.trim());
    }
}