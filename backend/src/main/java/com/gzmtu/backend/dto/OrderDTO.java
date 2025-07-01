package com.gzmtu.backend.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private Integer addressId;
    private String address;
    private Double totalPrice;
    private String status;
    private Timestamp createdAt;
    private List<OrderItemDTO> items;
    private String username;
    private String consignee;
    private String phone;
    private String fullAddress;
} 