package com.gzmtu.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreationDTO {
    private Integer userId;
    private Integer addressId;
    private Double totalPrice;
    private String status;
    private String orderNumber;
    private List<OrderItemCreationDTO> items;

    @Data
    public static class OrderItemCreationDTO {
        private Integer bookId;
        private int quantity;
        private double price;
    }
} 