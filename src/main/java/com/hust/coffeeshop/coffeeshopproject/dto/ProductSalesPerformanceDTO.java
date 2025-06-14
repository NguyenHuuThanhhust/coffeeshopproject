package com.hust.coffeeshop.coffeeshopproject.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesPerformanceDTO {
    private Long menuItemId;
    private String itemName;
    private String category;
    private int totalQuantitySold;
    private BigDecimal grossRevenueVND;
    private BigDecimal grossRevenueUSD;
    private int numberOfOrdersContainingItem;
}

