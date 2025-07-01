package com.gzmtu.backend.service;

import com.gzmtu.backend.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Integer id);
    Category addCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Integer id);
} 