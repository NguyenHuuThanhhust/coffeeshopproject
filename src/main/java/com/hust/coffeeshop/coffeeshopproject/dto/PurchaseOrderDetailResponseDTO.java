package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; 
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailResponseDTO {
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantityOrdered;
    private BigDecimal unitPrice;
}