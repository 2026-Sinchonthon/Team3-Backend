package com.sinchonthon.team3_backend.domain.tip;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name; // 🍚 먹고살기 · 🏠 집관리 · 🏥 아플 때 · 💼 알바 · 📚 공부/작업 · 💡 생활꿀팁

    @Builder
    public Category(String name) {
        this.name = name;
    }
}
