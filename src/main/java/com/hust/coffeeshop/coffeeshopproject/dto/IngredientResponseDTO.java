package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate; // Cần cho trường LastRestockDate

@Data // Tự động tạo getters, setters, toString, equals, hashCode
@NoArgsConstructor // Tự động tạo constructor không đối số
@AllArgsConstructor // Tự động tạo constructor với tất cả các trường
public class IngredientResponseDTO {
    private Long ingredientId; // ID của nguyên liệu, kiểu Long
    private String ingredientName;
    private String unitOfMeasure;
    private BigDecimal currentStock;
    private BigDecimal minStockLevel;
    private LocalDate lastRestockDate; // Ngày nhập kho gần nhất
}