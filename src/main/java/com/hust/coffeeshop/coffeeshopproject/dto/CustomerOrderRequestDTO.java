package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderRequestDTO {

    private Long customerId;
    private String customerName;
    private String phoneNumber;

    // SỬA TẠI ĐÂY: XÓA TRƯỜNG employeeId NẾU BẠN KHÔNG CẦN GỬI NÓ
    // private Long employeeId; // XÓA DÒNG NÀY

    private Long promotionId;

    private LocalDateTime orderTime;
    private LocalDateTime expectedPickupTime;

    private BigDecimal totalAmountUsd;
    private BigDecimal exchangeRate;
    private BigDecimal rankDiscount;
    private BigDecimal promotionDiscount;

    private String orderStatus;
    private String paymentMethod;
    private Boolean isPaid;
    private String Email;
    private String notes;

    private List<OrderDetailRequestDTO> orderDetails;

}