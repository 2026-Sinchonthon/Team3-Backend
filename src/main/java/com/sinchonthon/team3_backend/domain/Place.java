package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoPlaceId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public Place(String kakaoPlaceId, String name, String address, BigDecimal latitude, BigDecimal longitude, String phone) {
        this.kakaoPlaceId = kakaoPlaceId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
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
