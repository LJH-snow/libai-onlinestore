package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.AddressDTO;
import com.gzmtu.backend.entity.Address;
import com.gzmtu.backend.service.AddressService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping
    public ApiResponse<AddressDTO> addAddress(@RequestBody Address address) {
        return ApiResponse.success(addressService.addAddress(address));
    }

    @PutMapping
    public ApiResponse<AddressDTO> updateAddress(@RequestBody Address address) {
        return ApiResponse.success(addressService.updateAddress(address));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ApiResponse.success();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AddressDTO>> getAddressesByUserId(@PathVariable Integer userId) {
        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        // 查询每个地址是否被订单引用
        for (AddressDTO addr : addresses) {
            boolean canDelete = addressService.canDeleteAddress(addr.getId());
            // 通过反射或扩展DTO添加canDelete字段
            try {
                java.lang.reflect.Field f = addr.getClass().getDeclaredField("canDelete");
                f.setAccessible(true);
                f.set(addr, canDelete);
            } catch (Exception ignored) {}
        }
        return ApiResponse.success(addresses);
    }

    @PutMapping("/user/{userId}/default/{addressId}")
    public ApiResponse<Void> setDefaultAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return ApiResponse.success();
    }
} 