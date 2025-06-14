package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesSummaryDTO {
    private LocalDate saleDate;
    private int numberOfOrders;
    private BigDecimal totalRevenueVND;
    private BigDecimal totalRevenueUSD;
    private BigDecimal totalRankDiscountGivenVND;
    private BigDecimal totalPromotionDiscountGivenVND;
}
