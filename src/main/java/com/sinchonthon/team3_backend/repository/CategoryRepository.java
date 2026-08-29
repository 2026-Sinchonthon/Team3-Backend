package com.sinchonthon.team3_backend.repository;

import com.sinchonthon.team3_backend.domain.tip.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}