package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailRequestDTO {
    private Long ingredientId;
    private BigDecimal quantityOrdered;
    private BigDecimal unitPrice;
}