package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.CartItemDTO;
import com.gzmtu.backend.entity.CartItem;
import com.gzmtu.backend.service.CartService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/item")
    public ApiResponse<Void> addCartItem(@RequestBody CartItem cartItem) {
        cartService.addCartItem(cartItem);
        return ApiResponse.success();
    }

    @DeleteMapping("/item/{id}")
    public ApiResponse<Void> removeCartItem(@PathVariable Integer id) {
        cartService.removeCartItem(id);
        return ApiResponse.success();
    }

    @PutMapping("/item")
    public ApiResponse<Void> updateCartItem(@RequestBody CartItem cartItem) {
        cartService.updateCartItem(cartItem);
        return ApiResponse.success();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<CartItemDTO>> getCartItemsByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(cartService.getCartItemsByUserId(userId));
    }

    @DeleteMapping("/user/{userId}/clear")
    public ApiResponse<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ApiResponse.success();
    }
} 