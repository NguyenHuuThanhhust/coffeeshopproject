// src/main/java/com/hust/coffeeshop/coffeeshopproject/dto/EventPromotionRequestDTO.java
package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; // Sử dụng LocalDate

@Data
public class EventPromotionRequestDTO {
    private String promotionName;
    private String description;
    private String promotionType; // Thêm vào
    private BigDecimal value;
    private Integer remainingUses; // Thêm vào
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minOrderAmount; // Thêm vào

    // Trường 'status' ĐÃ BỊ LOẠI BỎ
}