package com.gzmtu.backend.service;

import com.gzmtu.backend.dto.CartItemDTO;
import com.gzmtu.backend.entity.CartItem;
import java.util.List;

public interface CartService {
    void addCartItem(CartItem cartItem);
    void removeCartItem(Integer cartItemId);
    void updateCartItem(CartItem cartItem);
    List<CartItemDTO> getCartItemsByUserId(Integer userId);
    void clearCart(Integer userId);
} 