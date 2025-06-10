package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailRequestDTO {
    private Integer menuItemId;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderDetailRequestDTO() {}
}