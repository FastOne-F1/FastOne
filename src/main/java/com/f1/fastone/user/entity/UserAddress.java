package com.f1.fastone.user.entity;

import com.f1.fastone.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_user_address")
public class UserAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String postalCode;
    @Column(length = 120)
    private String city;
    @Column
    private String address;
    @Column
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public UserAddress(String postalCode, String city, String address,
                       String addressDetail, User user) {
        this.postalCode = postalCode;
        this.city = city;
        this.address = address;
        this.addressDetail = addressDetail;
        this.user = user;
    }

    // 주소 업데이트 메서드
    public void updateAddress(String postalCode, String city, String address, String addressDetail) {
        if (postalCode != null) this.postalCode = postalCode;
        if (city != null) this.city = city;
        if (address != null) this.address = address;
        if (addressDetail != null) this.addressDetail = addressDetail;
    }

}