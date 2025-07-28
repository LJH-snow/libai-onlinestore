package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartId(Integer cartId);
    CartItem findByCartIdAndBookId(Integer cartId, Integer bookId);
} 