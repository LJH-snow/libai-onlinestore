package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface CategoryRepository extends JpaRepository<Category, Integer> {
} 