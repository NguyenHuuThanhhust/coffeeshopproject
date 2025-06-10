package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Yêu cầu Lombok hoặc tự viết getters/setters
import java.math.BigDecimal;

@Data
public class OrderDetailResponseDTO {
    private Integer orderDetailId;
    private Integer orderId; // ID của đơn hàng cha
    private Integer menuItemId;
    private String menuItemName; // Tên của món ăn (ví dụ)
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal discountAmount;
    // Có thể thêm subtotal nếu muốn hiển thị tổng tiền cho từng món
    // private BigDecimal subtotal;

    public OrderDetailResponseDTO() {}
}