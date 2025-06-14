package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; 
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class PurchaseOrderResponseDTO {
    private Long purchaseOrderId; 
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String orderStatus;
    private BigDecimal totalAmount;
    private Long supplierId; 
    private String supplierName;
    private String supplierContactPerson;
    private String supplierEmail;
    private String supplierPhoneNumber;
    private List<PurchaseOrderDetailResponseDTO> orderDetails;
}