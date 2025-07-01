package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface AddressRepository extends JpaRepository<Address, Integer> {
} 