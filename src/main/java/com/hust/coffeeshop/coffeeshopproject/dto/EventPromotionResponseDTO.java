package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPromotionResponseDTO {
    private Long promotionId;
    private String promotionName;
    private String description;
    private String promotionType;
    private BigDecimal value;
    private Integer remainingUses;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minOrderAmount;
}