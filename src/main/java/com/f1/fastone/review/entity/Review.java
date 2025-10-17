package com.f1.fastone.review.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.f1.fastone.common.entity.BaseEntity;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "p_review")
@SQLRestriction("deleted_at IS NULL")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private int score;

    @Column(length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "review_id")
    private UUID reviewId;

    @Column(length = 1000)
    private String replyContent;

    private LocalDateTime replyCreatedAt;

    private LocalDateTime replyUpdatedAt;

    public void update(int score, String content) {
        this.score = score;
        this.content = content;
    }

    public void addReply(String replyContent) {
        this.replyContent = replyContent;
        this.replyCreatedAt = LocalDateTime.now();
        this.replyUpdatedAt = LocalDateTime.now();
    }

    public void updateReply(String replyContent) {
        this.replyContent = replyContent;
        this.replyUpdatedAt = LocalDateTime.now();
    }

    public void deleteReply() {
        this.replyContent = null;
        this.replyCreatedAt = null;
        this.replyUpdatedAt = null;
    }
}