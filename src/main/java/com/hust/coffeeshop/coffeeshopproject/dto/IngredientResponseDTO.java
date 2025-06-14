package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponseDTO {
    private Long ingredientId;
    private String ingredientName;
    private String unitOfMeasure;
    private BigDecimal currentStock;
    private BigDecimal minStockLevel;
    private LocalDate lastRestockDate;
}