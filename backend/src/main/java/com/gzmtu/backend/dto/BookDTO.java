package com.gzmtu.backend.dto;

import lombok.Data;

@Data
public class BookDTO {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Double price;
    private Integer stock;
    private String description;
    private String cover;
    private Integer categoryId;
    private String status;
}