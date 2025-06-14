package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderResponseDTO {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Long promotionId;
    private String promotionName;
    private LocalDateTime orderTime;
    private LocalDateTime expectedPickupTime;
    private BigDecimal totalAmount;
    private BigDecimal totalAmountUsd;
    private BigDecimal exchangeRate;
    private BigDecimal rankDiscount;
    private BigDecimal promotionDiscount;
    private String orderStatus;
    private String paymentMethod;
    private Boolean isPaid;
    private String notes;

    private List<OrderDetailResponseDTO> orderDetails;

}