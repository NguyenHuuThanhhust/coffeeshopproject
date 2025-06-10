package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseOrderDetailRequestDTO {
    private Integer ingredientId;
    private BigDecimal quantityOrdered;
    private BigDecimal unitPrice;
}