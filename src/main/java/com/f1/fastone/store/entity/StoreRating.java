package com.f1.fastone.store.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store_rating")
public class StoreRating {
    @Id
    @Column(name="store_id")
    private UUID id;

    @OneToOne(fetch=FetchType.LAZY)
    @MapsId
    @JoinColumn(name="store_id")
    private Store store;

    @Column(nullable=false)
    private int reviewCount = 0;

    @Column(nullable=false, precision=2, scale=1)
    private BigDecimal scoreAvg = BigDecimal.ZERO;
}