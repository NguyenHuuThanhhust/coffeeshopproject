package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequestDTO {
    private String ingredientName;
    private String unitOfMeasure;
    private BigDecimal currentStock;
    private BigDecimal minStockLevel;
}