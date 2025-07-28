package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUserId(Integer userId);
} 