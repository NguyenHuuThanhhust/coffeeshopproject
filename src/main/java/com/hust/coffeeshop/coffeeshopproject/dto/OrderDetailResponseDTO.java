package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor
import lombok.AllArgsConstructor; // Thêm AllArgsConstructor
import java.math.BigDecimal;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class OrderDetailResponseDTO {
    private Long orderDetailId; // Đã sửa từ Integer sang Long
    private Long orderId; // Đã sửa từ Integer sang Long
    private Long menuItemId; // Đã sửa từ Integer sang Long
    private String menuItemName;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal discountAmount;
}