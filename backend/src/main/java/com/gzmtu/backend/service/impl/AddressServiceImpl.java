package com.gzmtu.backend.service.impl;

import com.gzmtu.backend.dto.AddressDTO;
import com.gzmtu.backend.entity.Address;
import com.gzmtu.backend.repository.AddressRepository;
import com.gzmtu.backend.repository.OrderRepository;
import com.gzmtu.backend.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public AddressDTO addAddress(Address address) {
        // 如果本次新增的是默认地址，先把该用户的其他地址全部设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            List<Address> userAddresses = addressRepository.findAll().stream()
                    .filter(a -> a.getUserId().equals(address.getUserId()))
                    .collect(Collectors.toList());
            for (Address a : userAddresses) {
                if (a.getIsDefault() != null && a.getIsDefault() == 1) {
                    a.setIsDefault(0);
                    addressRepository.save(a);
                }
            }
        }
        Address saved = addressRepository.save(address);
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(saved, dto);
        return dto;
    }

    @Override
    public AddressDTO updateAddress(Address address) {
        Address saved = addressRepository.save(address);
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(saved, dto);
        return dto;
    }

    @Override
    public void deleteAddress(Integer id) {
        try {
        addressRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("该地址已被订单引用，无法删除");
        }
    }

    @Override
    public List<AddressDTO> getAddressesByUserId(Integer userId) {
        return addressRepository.findAll().stream()
                .filter(address -> address.getUserId().equals(userId))
                .map(address -> {
                    AddressDTO dto = new AddressDTO();
                    BeanUtils.copyProperties(address, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void setDefaultAddress(Integer userId, Integer addressId) {
        addressRepository.findAll().stream()
                .filter(address -> address.getUserId().equals(userId))
                .forEach(address -> {
                    address.setIsDefault(address.getId().equals(addressId) ? 1 : 0);
                    addressRepository.save(address);
                });
    }

    @Override
    public boolean canDeleteAddress(Integer id) {
        // 查询是否有订单引用该地址
        return orderRepository.findAll().stream().noneMatch(order -> id.equals(order.getAddressId()));
    }
} 