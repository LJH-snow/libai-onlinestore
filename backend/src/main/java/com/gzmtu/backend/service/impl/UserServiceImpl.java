/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:24:55
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 16:56:01
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\service\impl\UserServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.service.impl;

import com.gzmtu.backend.dto.UserDTO;
import com.gzmtu.backend.dto.UserUpdateDTO;
import com.gzmtu.backend.entity.User;
import com.gzmtu.backend.repository.UserRepository;
import com.gzmtu.backend.service.UserService;
import com.gzmtu.backend.security.JwtTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDTO register(User user) {
        // 为新用户设置默认值
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // 默认角色为 'USER'（大写）
        }
        user.setStatus(1); // 默认状态为 1 (启用)
        user.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis())); // 设置创建时间
        if (user.getRealName() == null || user.getRealName().isEmpty()) {
            user.setRealName(user.getUsername());
        }
        // 明文存储密码
        // user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // realName 字段已由前端传递，直接保存
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO login(String loginType, String identifier, String password) {
        User user;
        switch (loginType) {
            case "email":
                user = userRepository.findByEmail(identifier).orElse(null);
                break;
            case "phone":
                user = userRepository.findByPhone(identifier).orElse(null);
                break;
            case "username":
            default:
                user = userRepository.findByUsername(identifier).orElse(null);
                break;
        }

        if (user != null && password.equals(user.getPassword())) {
            return jwtTokenUtil.generateUserDTO(user);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User has no role assigned: " + username);
        }
        // 确保角色格式正确，Spring Security期望的是 ROLE_USER, ROLE_ADMIN 等
        String roleWithPrefix = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole().toUpperCase();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singletonList(authority));
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateUser(Integer id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }

        // 更新基本信息
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }

        // 如果提供了新密码，验证原密码并更新
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 这里应该验证原密码，但为了简化，我们直接更新
            // 在实际应用中，应该先验证原密码是否正确
            existingUser.setPassword(user.getPassword());
        }

        User savedUser = userRepository.save(existingUser);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUserInfo(Integer id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }

        // 更新基本信息
        if (userUpdateDTO.getUsername() != null) {
            existingUser.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            existingUser.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhone() != null) {
            existingUser.setPhone(userUpdateDTO.getPhone());
        }

        // 如果提供了新密码，验证原密码并更新
        if (userUpdateDTO.getNewPwd() != null && !userUpdateDTO.getNewPwd().isEmpty()) {
            // 验证原密码
            if (userUpdateDTO.getOldPwd() == null || !userUpdateDTO.getOldPwd().equals(existingUser.getPassword())) {
                throw new RuntimeException("原密码不正确");
            }
            // 验证新密码确认
            if (!userUpdateDTO.getNewPwd().equals(userUpdateDTO.getConfirmPwd())) {
                throw new RuntimeException("新密码确认不匹配");
            }
            existingUser.setPassword(userUpdateDTO.getNewPwd());
        }

        User savedUser = userRepository.save(existingUser);
        return convertToDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUserStatus(Integer id, Integer status) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        user.setStatus(status);
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public boolean deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt().toString());
        }
        return dto;
    }
}