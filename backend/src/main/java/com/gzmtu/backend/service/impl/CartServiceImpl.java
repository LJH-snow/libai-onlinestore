/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:28:14
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-21 16:45:48
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\service\impl\CartServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.service.impl;

import com.gzmtu.backend.dto.CartItemDTO;
import com.gzmtu.backend.entity.Cart;
import com.gzmtu.backend.entity.CartItem;
import com.gzmtu.backend.repository.CartItemRepository;
import com.gzmtu.backend.repository.CartRepository;
import com.gzmtu.backend.repository.BookRepository;
import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional
    public void addCartItem(CartItem cartItem) {
        Integer userId = cartItem.getCartId(); // Note: This is userId from frontend
        Integer bookId = cartItem.getBookId();
        Integer quantity = cartItem.getQuantity();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        Double unitPrice = book.getPrice();

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setBookId(bookId);
            newItem.setQuantity(quantity);
            newItem.setPrice(unitPrice); // Set the price fetched from the book table
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void removeCartItem(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void updateCartItem(CartItem cartItem) {
        CartItem existingItem = cartItemRepository.findById(cartItem.getId()).orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(cartItem.getQuantity());
            cartItemRepository.save(existingItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItemsByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return new ArrayList<>();
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        return cartItems.stream().map(item -> {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setCartId(item.getCartId());
            dto.setBookId(item.getBookId());
            dto.setQuantity(item.getQuantity());
            if (book != null) {
                dto.setTitle(book.getTitle());
                dto.setCover(book.getCover());
                dto.setAuthor(book.getAuthor());
                dto.setPrice(book.getPrice());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(cartItems);
        }
    }
} 