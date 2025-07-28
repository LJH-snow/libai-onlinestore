package com.gzmtu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "address_id")
    private Integer addressId;
    @Column(name = "total_price")
    private Double totalPrice;
    private String status;
    @Column(name = "created_at")
    private Timestamp createdAt;
} 