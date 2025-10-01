package com.f1.fastone.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store_category")
public class StoreCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80, unique = true)
    private String storeCategoryName;

    // Store store 필드와 store_id JoinColumn을 제거하여 독립적인 카테고리 엔티티로 복구
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;

    // StoreCategory 수정용
    public void updateName(String storeCategoryName) {
        this.storeCategoryName = storeCategoryName;
    }

}