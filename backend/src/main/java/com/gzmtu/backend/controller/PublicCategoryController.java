package com.gzmtu.backend.controller;

import com.gzmtu.backend.entity.Category;
import com.gzmtu.backend.service.CategoryService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/category") // 路径修改为 /api/public
public class PublicCategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }
} 