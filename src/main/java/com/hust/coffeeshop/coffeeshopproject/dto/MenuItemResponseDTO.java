package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Tự động tạo getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Tự động tạo constructor không đối số
import lombok.AllArgsConstructor; // Tự động tạo constructor với tất cả các trường

import java.math.BigDecimal; // Cần cho trường price và minSellingPrice

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponseDTO {
    private Long menuItemId; // ID của món, kiểu Long
    private String itemName;
    private String description;
    private BigDecimal price; // Giá của món, kiểu BigDecimal
    private String category;
    private String status; // Ví dụ: "Available", "Unavailable"
    private Boolean isAvailable;
    private String imageUrl; // URL hình ảnh
    // private byte[] thumbnail; // Thường không trả về trực tiếp byte[] trong Response DTO
    private BigDecimal minSellingPrice; // Giá bán tối thiểu, kiểu BigDecimal
}