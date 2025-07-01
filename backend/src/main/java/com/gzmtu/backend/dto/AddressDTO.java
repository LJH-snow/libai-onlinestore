package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Integer id;
    private Integer userId;
    private String address;
    private Integer isDefault;
    private Boolean canDelete;
} 