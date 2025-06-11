package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal; // Đảm bảo đúng kiểu dữ liệu

@Data
public class PurchaseOrderRequestDTO {
    // Không cần supplierId ở đây nếu bạn truyền qua @RequestParam
    // private Integer supplierId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    // actualDeliveryDate thường để null khi tạo
    private BigDecimal totalAmount; // Có thể được tính toán ở backend, nhưng vẫn có thể gửi lên
    private String orderStatus;
    private LocalDate actualDeliveryDate;
    private List<PurchaseOrderDetailRequestDTO> purchaseOrderDetails;
}