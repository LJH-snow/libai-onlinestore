/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:18:07
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-21 16:40:15
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\entity\Book.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Double price;
    private Integer stock;
    private String description;
    private String cover;
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(columnDefinition = "varchar(255) default 'on'")
    private String status;
    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;
} 