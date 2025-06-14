package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long customerId;
    private String customerName;
    private String phoneNumber;
    private String email;
    private LocalDate dateJoined;
    private BigDecimal totalSpent;
    private String rankName;
}