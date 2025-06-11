package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor
import lombok.AllArgsConstructor; // Thêm AllArgsConstructor
import java.math.BigDecimal;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class PurchaseOrderDetailRequestDTO {
    private Long ingredientId; // Đã sửa từ Integer sang Long
    private BigDecimal quantityOrdered;
    private BigDecimal unitPrice;
}