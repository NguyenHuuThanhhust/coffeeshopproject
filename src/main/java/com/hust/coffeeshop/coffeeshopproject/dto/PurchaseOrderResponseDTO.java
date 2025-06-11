package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Thêm import này
import lombok.NoArgsConstructor; // Thêm import này
import lombok.AllArgsConstructor; // Thêm import này
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class PurchaseOrderResponseDTO {
    private Long purchaseOrderId; // SỬA TẠI ĐÂY: Integer -> Long
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String orderStatus;
    private BigDecimal totalAmount;

    private Long supplierId; // SỬA TẠI ĐÂY: Integer -> Long
    private String supplierName;
    private String supplierContactPerson;
    private String supplierEmail;
    private String supplierPhoneNumber;

    private List<PurchaseOrderDetailResponseDTO> orderDetails;
}