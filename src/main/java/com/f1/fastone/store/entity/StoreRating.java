package com.f1.fastone.store.entity;

import jakarta.persistence.*;

import java.math.RoundingMode;
import java.util.UUID;
import lombok.*;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store_rating")
public class StoreRating {

    @Id
    @Column(name = "store_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal scoreAvg = BigDecimal.ZERO;

    @Builder
    public StoreRating(Store store, int reviewCount, BigDecimal scoreAvg) {
        this.store = store;
        this.reviewCount = reviewCount;
        this.scoreAvg = scoreAvg != null ? scoreAvg : BigDecimal.ZERO;
    }

    public void addScore(int newScore) {
        int newCount = this.reviewCount + 1;
        this.scoreAvg = calculateNewAverage(this.scoreAvg, this.reviewCount, newScore, newCount);
        this.reviewCount = newCount;
    }

    public void updateScore(int oldScore, int newScore) {
        if (reviewCount <= 0) return;
        this.scoreAvg = calculateNewAverageOnUpdate(this.scoreAvg, this.reviewCount, oldScore, newScore);
    }

    public void removeScore(int oldScore) {
        int newCount = this.reviewCount - 1;
        if (newCount <= 0) {
            this.reviewCount = 0;
            this.scoreAvg = BigDecimal.ZERO;
            return;
        }
        this.scoreAvg = calculateNewAverageOnRemove(this.scoreAvg, this.reviewCount, oldScore, newCount);
        this.reviewCount = newCount;
    }

    private BigDecimal calculateNewAverage(BigDecimal currentAvg, int currentCount, int newScore, int newCount) {
        return currentAvg.multiply(BigDecimal.valueOf(currentCount))
            .add(BigDecimal.valueOf(newScore))
            .divide(BigDecimal.valueOf(newCount), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateNewAverageOnUpdate(BigDecimal currentAvg, int count, int oldScore, int newScore) {
        return currentAvg.multiply(BigDecimal.valueOf(count))
            .subtract(BigDecimal.valueOf(oldScore))
            .add(BigDecimal.valueOf(newScore))
            .divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateNewAverageOnRemove(BigDecimal currentAvg, int currentCount, int oldScore, int newCount) {
        return currentAvg.multiply(BigDecimal.valueOf(currentCount))
            .subtract(BigDecimal.valueOf(oldScore))
            .divide(BigDecimal.valueOf(newCount), 1, RoundingMode.HALF_UP);
    }
}