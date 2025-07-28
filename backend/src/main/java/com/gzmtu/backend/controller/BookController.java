/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:29:05
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-21 20:30:27
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\controller\BookController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.controller;

import com.gzmtu.backend.dto.BookDTO;
import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.service.BookService;
import com.gzmtu.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ApiResponse<Page<BookDTO>> getBooks(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String title,
                                               @RequestParam(required = false) Integer categoryId) {
        Page<BookDTO> books = bookService.searchBooks(title, categoryId, page, size);
        return ApiResponse.success(books);
    }

    @PostMapping
    public ApiResponse<Book> addBook(@RequestBody Book book) {
        return ApiResponse.success(bookService.addBook(book));
    }

    @PutMapping("/{id}")
    public ApiResponse<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        book.setId(id);
        return ApiResponse.success(bookService.updateBook(book));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Integer id) {
        Book book = bookService.getBookById(id);
        return ApiResponse.success(book);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Book> updateBookStatus(
            @PathVariable Integer id,
            @RequestParam String status
    ) {
        return ApiResponse.success(bookService.updateBookStatus(id, status));
    }
} 