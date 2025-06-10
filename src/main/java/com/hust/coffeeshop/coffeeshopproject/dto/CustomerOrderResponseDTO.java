package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Yêu cầu Lombok hoặc tự viết getters/setters
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Integer orderId;
    private Integer customerId;
    private String customerName; // Để hiển thị tên khách hàng
    private Integer employeeId;
    private String employeeName; // Để hiển thị tên nhân viên (ví dụ)
    private Integer promotionId;
    private String promotionName; // Để hiển thị tên khuyến mãi (ví dụ)

    private LocalDateTime orderTime;
    private LocalDateTime expectedPickupTime;
    private BigDecimal totalAmount;
    private BigDecimal exchangeRate;
    private BigDecimal rankDiscount;
    private BigDecimal promotionDiscount;
    private String orderStatus;
    private String paymentMethod;
    private Boolean isPaid;
    private String notes;

    private List<OrderDetailResponseDTO> orderDetails; // Danh sách chi tiết đơn hàng

    public CustomerOrderResponseDTO() {}
}