package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder; 
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrderDetail; 
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier; 
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO createPurchaseOrder(Long supplierId, List<PurchaseOrderDetailRequestDTO> details); 
    PurchaseOrderResponseDTO updatePurchaseOrderStatus(Long id, String status); 
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    Optional<PurchaseOrderResponseDTO> getPurchaseOrderById(Long id); 
    PurchaseOrderResponseDTO updatePurchaseOrder(Long id, PurchaseOrderRequestDTO requestDTO); 
    void deletePurchaseOrder(Long id); 
    Optional<Supplier> getSupplierByName(String supplierName);


    PurchaseOrderResponseDTO convertToPurchaseOrderDTO(PurchaseOrder purchaseOrder);
    PurchaseOrderDetailResponseDTO convertToPurchaseOrderDetailDTO(PurchaseOrderDetail detail);
}