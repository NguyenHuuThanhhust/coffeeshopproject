package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import java.util.List;

@Data
public class SupplierResponseDTO {
    private Integer supplierId;
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;

    private List<PurchaseOrderResponseDTO> purchaseOrders;
}