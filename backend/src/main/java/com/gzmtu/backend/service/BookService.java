package com.gzmtu.backend.service;

import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.dto.BookDTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface BookService {
    Page<Book> searchAdminBooks(String title, Integer categoryId, int page, int size);
    Page<Book> searchPublicBooks(Integer categoryId, int page, int size);
    Book addBook(Book book);
    Book updateBook(Book book);
    void deleteBook(Integer id);
    Book getBookById(Integer id);
    List<Book> getBooksByCategory(Integer categoryId);
    Book updateBookStatus(Integer id, String status);
    boolean canDeleteBook(Integer id);
    Page<BookDTO> searchBooks(String title, Integer categoryId, int page, int size);
} 