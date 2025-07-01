package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Integer id;
    private Integer cartId;
    private Integer bookId;
    private Integer quantity;
    private String title;
    private String cover;
    private String author;
    private Double price;
} 