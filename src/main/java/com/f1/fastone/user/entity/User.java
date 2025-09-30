package com.f1.fastone.user.entity;

import com.f1.fastone.cart.entity.Cart;
import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.store.entity.Store;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {
    @Id
    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 100)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<UserAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Store> stores = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public User(String username, String nickname, String email, String password, UserRole role, Boolean isPublic, String phoneNumber) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isPublic = (isPublic != null) ? isPublic : true;
        this.phoneNumber = phoneNumber;
    }
}