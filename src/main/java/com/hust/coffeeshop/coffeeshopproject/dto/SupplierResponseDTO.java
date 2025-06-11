package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Thêm import này
import lombok.NoArgsConstructor; // Thêm import này
import lombok.AllArgsConstructor; // Thêm import này
import java.util.List;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class SupplierResponseDTO {
    private Long supplierId; // SỬA TẠI ĐÂY: Integer -> Long
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;

    // Cẩn thận với vòng lặp nếu PurchaseOrderResponseDTO cũng có SupplierResponseDTO
    // Bạn có thể chỉ trả về list of purchaseOrderIds nếu không muốn detail
    private List<PurchaseOrderResponseDTO> purchaseOrders;
}