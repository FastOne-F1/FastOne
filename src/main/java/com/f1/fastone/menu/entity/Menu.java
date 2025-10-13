package com.f1.fastone.menu.entity;

import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_menu")
public class Menu extends BaseEntity {

    public Menu(String name,
                String description,
                int price,
                boolean soldOut,
                String imageUrl,
                Store store,
                MenuCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.soldOut = soldOut;
        this.imageUrl = imageUrl;
        this.store = store;
        this.category = category;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean soldOut;

    @Column(nullable = false)
    private int price;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    public void update(String name, String description, int price, boolean soldOut, String imageUrl, MenuCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.soldOut = soldOut;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void updateSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }
}