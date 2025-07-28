package com.gzmtu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String email;
    private String phone;
    private String password;
    private String role;
    private Integer status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "real_name")
    private String realName;
} 