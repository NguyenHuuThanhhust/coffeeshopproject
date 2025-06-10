package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Yêu cầu Lombok hoặc tự viết getters/setters
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerOrderRequestDTO {
    private Integer customerId;
    private Integer employeeId; // Có thể null
    private Integer promotionId; // Có thể null

    private LocalDateTime orderTime;
    private LocalDateTime expectedPickupTime;

    // Các trường này có thể được tính toán ở backend, nhưng cũng có thể gửi lên
    private BigDecimal totalAmountUsd;
    private BigDecimal exchangeRate;
    private BigDecimal rankDiscount;
    private BigDecimal promotionDiscount;

    private String orderStatus; // Ví dụ: "PENDING", "PROCESSING", "COMPLETED", "CANCELLED"
    private String paymentMethod; // Ví dụ: "CASH", "CARD", "BANK_TRANSFER"
    private Boolean isPaid;
    private String notes;

    private List<OrderDetailRequestDTO> orderDetails; // Danh sách chi tiết đơn hàng

    public CustomerOrderRequestDTO() {}
}