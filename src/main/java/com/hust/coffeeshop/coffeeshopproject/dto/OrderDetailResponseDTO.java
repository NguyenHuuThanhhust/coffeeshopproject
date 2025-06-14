package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 
import java.math.BigDecimal;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class OrderDetailResponseDTO {
    private Long orderDetailId; 
    private Long orderId; 
    private Long menuItemId; 
    private String menuItemName;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal discountAmount;
}