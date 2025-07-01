package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer id;
    private Integer bookId;
    private String bookTitle; // 书名
    private String bookCover; // 封面
    private int quantity;
    private double price;
} 