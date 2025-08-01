package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
} 