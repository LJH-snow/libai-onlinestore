package com.gzmtu.backend.controller;

import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.service.BookService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/book")
public class PublicBookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ApiResponse<Page<Book>> getAvailableBooks(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        // 调用searchPublicBooks，只筛选在售的书籍
        Page<Book> books = bookService.searchPublicBooks(categoryId, page, size);
        return ApiResponse.success(books);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Integer id) {
        return ApiResponse.success(bookService.getBookById(id));
    }
} 