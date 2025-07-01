/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:25:11
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 16:40:36
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\controller\UserController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.UserDTO;
import com.gzmtu.backend.dto.UserUpdateDTO;
import com.gzmtu.backend.entity.User;
import com.gzmtu.backend.service.UserService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody User user) {
        return ApiResponse.success(userService.register(user));
    }

    @PostMapping("/login")
    public ApiResponse<UserDTO> login(@RequestBody User user) {
        // 判断登录类型
        String identifier = user.getUsername();
        String loginType;
        if (identifier == null) {
            return ApiResponse.error(400, "账号不能为空");
        } else if (identifier.matches("^1[3-9]\\d{9}$")) {
            loginType = "phone";
        } else if (identifier.contains("@")) {
            loginType = "email";
        } else {
            loginType = "username";
        }
        UserDTO dto = userService.login(loginType, identifier, user.getPassword());
        if (dto == null) {
            return ApiResponse.error(403, "账号或密码错误");
        }
        return ApiResponse.success(dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Integer id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserDTO updatedUser = userService.updateUserInfo(id, userUpdateDTO);
            if (updatedUser == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success(updatedUser);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<UserDTO> updateUserStatus(@PathVariable Integer id, @RequestParam Integer status) {
        UserDTO updated = userService.updateUserStatus(id, status);
        if (updated == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(updated);
    }

    @GetMapping("/all")
    public ApiResponse<Page<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String role) {
        List<UserDTO> all = userService.getAllUsers();
        // 过滤
        if (keyword != null && !keyword.isEmpty()) {
            all = all.stream().filter(u ->
                (u.getUsername() != null && u.getUsername().contains(keyword)) ||
                (u.getEmail() != null && u.getEmail().contains(keyword))
            ).toList();
        }
        if (role != null && !role.isEmpty()) {
            all = all.stream().filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase(role)).toList();
        }
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<UserDTO> pageList = start > end ? List.of() : all.subList(start, end);
        Page<UserDTO> result = new PageImpl<>(pageList, PageRequest.of(page, size), all.size());
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ApiResponse.success(null);
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    @PostMapping("")
    public ApiResponse<UserDTO> addUser(@RequestBody User user) {
        return ApiResponse.success(userService.register(user));
    }

    // 其他接口

} 