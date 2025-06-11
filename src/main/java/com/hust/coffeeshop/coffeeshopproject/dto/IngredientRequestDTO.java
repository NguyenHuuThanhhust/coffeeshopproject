package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
// Không cần LocalDate lastRestockDate ở đây vì nó thường được backend tự động cập nhật
// khi nhập kho, không phải là input trực tiếp khi tạo/cập nhật thông tin nguyên liệu cơ bản.

@Data // Tự động tạo getters, setters, toString, equals, hashCode
@NoArgsConstructor // Tự động tạo constructor không đối số
@AllArgsConstructor // Tự động tạo constructor với tất cả các trường
public class IngredientRequestDTO {
    // Không cần ingredientId khi tạo mới, DB sẽ tự sinh
    // Khi update, ID sẽ được truyền qua @PathVariable

    private String ingredientName;
    private String unitOfMeasure;
    private BigDecimal currentStock; // Có thể được dùng để cập nhật số lượng ban đầu hoặc thủ công
    private BigDecimal minStockLevel;
}