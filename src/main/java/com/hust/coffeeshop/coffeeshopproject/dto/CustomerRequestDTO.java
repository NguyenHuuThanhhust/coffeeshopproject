package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {

    private String customerName;

    private String phoneNumber;

    private String email;

    private LocalDate dateJoined;

    private BigDecimal totalSpent;
    private String rankName;
}