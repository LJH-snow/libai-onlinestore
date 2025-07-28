package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String email;
    private String phone;
    private String oldPwd;
    private String newPwd;
    private String confirmPwd;
} 