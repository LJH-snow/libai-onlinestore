package com.gzmtu.backend.service;

import com.gzmtu.backend.dto.UserDTO;
import com.gzmtu.backend.dto.UserUpdateDTO;
import com.gzmtu.backend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO register(User user);
    UserDTO login(String loginType, String identifier, String password);
    UserDTO getUserById(Integer id);
    UserDTO updateUser(Integer id, User user);
    UserDTO updateUserInfo(Integer id, UserUpdateDTO userUpdateDTO);
    List<UserDTO> getAllUsers();
    UserDTO updateUserStatus(Integer id, Integer status);
    boolean deleteUserById(Integer id);
}