/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 20:27:17
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 15:06:06
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\controller\CategoryController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.CategoryDTO;
import com.gzmtu.backend.entity.Category;
import com.gzmtu.backend.repository.CategoryRepository;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/all")
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> list = categoryRepository.findAll().stream().map(category -> {
            CategoryDTO dto = new CategoryDTO();
            BeanUtils.copyProperties(category, dto);
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }
} 