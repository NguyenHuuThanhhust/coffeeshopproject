package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Thêm import này
import lombok.NoArgsConstructor; // Thêm import này
import lombok.AllArgsConstructor; // Thêm import này
import java.math.BigDecimal;

@Data // Thay thế các getters/setters thủ công
@NoArgsConstructor // Thay thế constructor không đối số
@AllArgsConstructor // Thay thế constructor có đối số
public class PurchaseOrderDetailResponseDTO {
    // Không cần ID của PurchaseOrder ở đây để tránh vòng lặp

    private Long ingredientId; // SỬA TẠI ĐÂY: Integer -> Long
    private String ingredientName;
    private BigDecimal quantityOrdered; // SỬA TẠI ĐÂY: Double -> BigDecimal
    private BigDecimal unitPrice;
}