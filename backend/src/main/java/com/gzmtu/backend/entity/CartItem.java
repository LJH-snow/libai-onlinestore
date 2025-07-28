package com.gzmtu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "cart_id")
    private Integer cartId;
    @Column(name = "book_id")
    private Integer bookId;
    private Integer quantity;
    private Double price;
} 