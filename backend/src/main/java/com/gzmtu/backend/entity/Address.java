/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:19:49
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-24 00:28:02
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\entity\Address.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    private String address;
    @Column(name = "is_default")
    private Integer isDefault;

    public void setIsDefault(Object isDefault) {
        if (isDefault instanceof Boolean) {
            this.isDefault = (Boolean) isDefault ? 1 : 0;
        } else if (isDefault instanceof Number) {
            this.isDefault = ((Number) isDefault).intValue();
        } else if (isDefault != null) {
            this.isDefault = Integer.parseInt(isDefault.toString());
        } else {
            this.isDefault = 0;
        }
    }
} 