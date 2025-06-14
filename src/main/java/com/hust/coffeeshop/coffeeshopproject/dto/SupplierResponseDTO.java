package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; 
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private Long supplierId;
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private List<PurchaseOrderResponseDTO> purchaseOrders;
}