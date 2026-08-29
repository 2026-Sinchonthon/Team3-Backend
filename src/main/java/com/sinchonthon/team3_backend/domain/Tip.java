package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tips")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDate visitedAt;

    @Column(nullable = false)
    private Instant validUntil;

    @Column(nullable = false)
    private boolean isFiltered;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public Tip(User user, Place place, Category category, String title, String content, LocalDate visitedAt, Instant validUntil) {
        this.user = user;
        this.place = place;
        this.category = category;
        this.title = title;
        this.content = content;
        this.visitedAt = visitedAt;
        this.validUntil = validUntil;
        this.isFiltered = false;
    }

    public void updateFiltered(boolean isFiltered) {
        this.isFiltered = isFiltered;
    }

    @PrePersist
    void createTimestamps() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
