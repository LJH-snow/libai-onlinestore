package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private String realName;
    private String token;
    private String createdAt;
} 