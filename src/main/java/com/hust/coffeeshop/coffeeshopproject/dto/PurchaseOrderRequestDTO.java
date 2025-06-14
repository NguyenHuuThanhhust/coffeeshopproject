package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Data
public class PurchaseOrderRequestDTO {
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDate actualDeliveryDate;
    private List<PurchaseOrderDetailRequestDTO> purchaseOrderDetails;
}