package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRankDTO {
    private String rankName;
    private BigDecimal pointFrom;
    private BigDecimal pointTo;
    private BigDecimal discountRate;
    private BigDecimal loyaltyPointRate;
}