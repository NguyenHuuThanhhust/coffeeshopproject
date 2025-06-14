package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequestDTO {
    private String itemName;
    private String description;
    private BigDecimal price;
    private String category;
    private String status;
    private Boolean isAvailable;
    private String imageUrl;
    private BigDecimal minSellingPrice;
}