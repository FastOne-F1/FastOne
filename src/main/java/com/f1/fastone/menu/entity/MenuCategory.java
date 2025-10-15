package com.f1.fastone.menu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_menu_category")
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable=false, length=80)
    private String menuCategoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    // DTO -> Entity 변환 시 필요한 생성자
    public MenuCategory(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public void updateName(String menuCategoryName) {
        this.menuCategoryName =  menuCategoryName;
    }
}