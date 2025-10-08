package com.f1.fastone.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShipToDto {
    String name;
    String phone;
    String postalCode;
    String city;
    String address;
    String addressDetail;
}
