package com.f1.fastone.store.entity;

import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "p_store")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 10)
    private String postalCode;
    @Column(length = 120)
    private String city;
    @Column
    private String address;
    @Column
    private String addressDetail;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    private LocalTime openTime;
    private LocalTime closeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "store")
    private List<Menu> menus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StoreCategory category;

    @OneToOne(mappedBy = "store")
    private StoreRating storeRating;

    public void addStoreRating(StoreRating storeRating) {
        this.storeRating = storeRating;
    }

    public void update(String name, String phone, String postalCode, String city,
                       String address, String addressDetail, BigDecimal latitude,
                       BigDecimal longitude, LocalTime openTime, LocalTime closeTime,
                       StoreCategory category) {
        this.name = name;
        this.phone = phone;
        this.postalCode = postalCode;
        this.city = city;
        this.address = address;
        this.addressDetail = addressDetail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.category = category;
    }
}