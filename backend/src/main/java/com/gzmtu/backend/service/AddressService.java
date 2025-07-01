package com.gzmtu.backend.service;

import com.gzmtu.backend.dto.AddressDTO;
import com.gzmtu.backend.entity.Address;
import java.util.List;

public interface AddressService {
    AddressDTO addAddress(Address address);
    AddressDTO updateAddress(Address address);
    void deleteAddress(Integer id);
    List<AddressDTO> getAddressesByUserId(Integer userId);
    void setDefaultAddress(Integer userId, Integer addressId);
    boolean canDeleteAddress(Integer id);
} 