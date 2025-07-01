package com.gzmtu.backend.service.impl;

import com.gzmtu.backend.dto.BookDTO;
import com.gzmtu.backend.entity.Book;
import com.gzmtu.backend.repository.BookRepository;
import com.gzmtu.backend.repository.OrderItemRepository;
import com.gzmtu.backend.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Page<Book> searchAdminBooks(String title, Integer categoryId, int page, int size) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return bookRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public Page<Book> searchPublicBooks(Integer categoryId, int page, int size) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), "on")); // Always filter for 'on' status
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return bookRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        // 先查出原有 Book
        Book existing = bookRepository.findById(book.getId()).orElse(null);
        if (existing == null) {
            throw new RuntimeException("Book not found with id: " + book.getId());
        }
        // 只更新有值的字段
        if (book.getTitle() != null) existing.setTitle(book.getTitle());
        if (book.getAuthor() != null) existing.setAuthor(book.getAuthor());
        if (book.getPublisher() != null) existing.setPublisher(book.getPublisher());
        if (book.getIsbn() != null) existing.setIsbn(book.getIsbn());
        if (book.getPrice() != null) existing.setPrice(book.getPrice());
        if (book.getStock() != null) existing.setStock(book.getStock());
        if (book.getDescription() != null) existing.setDescription(book.getDescription());
        if (book.getCover() != null) existing.setCover(book.getCover());
        if (book.getCategoryId() != null) existing.setCategoryId(book.getCategoryId());
        // status 字段只有在前端传递时才更新，否则保持原值
        if (book.getStatus() != null) existing.setStatus(book.getStatus());
        return bookRepository.save(existing);
    }

    @Override
    public void deleteBook(Integer id) {
        try {
            bookRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("该图书已被订单引用，无法删除");
        }
    }

    @Override
    public Book getBookById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public List<Book> getBooksByCategory(Integer categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    @Override
    public Book updateBookStatus(Integer id, String status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        book.setStatus(status);
        return bookRepository.save(book);
    }

    @Override
    public boolean canDeleteBook(Integer id) {
        // 查询是否有订单项引用该图书
        return orderItemRepository.findByBookId(id).isEmpty();
    }

    @Override
    public Page<BookDTO> searchBooks(String title, Integer categoryId, int page, int size) {
        Specification<Book> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<Book> bookPage = bookRepository.findAll(spec, PageRequest.of(page, size));
        List<BookDTO> dtoList = new ArrayList<>();
        for (Book book : bookPage.getContent()) {
            BookDTO dto = new BookDTO();
            org.springframework.beans.BeanUtils.copyProperties(book, dto);
            dto.setStatus(book.getStatus());
            dtoList.add(dto);
        }
        return new org.springframework.data.domain.PageImpl<>(dtoList, bookPage.getPageable(), bookPage.getTotalElements());
    }
}