package com.gzmtu.backend.repository;

import com.gzmtu.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    List<Book> findByTitleContaining(String keyword);
    List<Book> findByCategoryId(Integer categoryId);
}