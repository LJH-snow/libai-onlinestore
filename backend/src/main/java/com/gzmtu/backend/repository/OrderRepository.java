package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
} 