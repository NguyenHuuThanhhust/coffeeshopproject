package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor
import lombok.AllArgsConstructor; // Thêm AllArgsConstructor
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class EventPromotionRequestDTO {
    private String promotionName;
    private String description;
    private String promotionType;
    private BigDecimal value;
    private Integer remainingUses;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minOrderAmount;
}