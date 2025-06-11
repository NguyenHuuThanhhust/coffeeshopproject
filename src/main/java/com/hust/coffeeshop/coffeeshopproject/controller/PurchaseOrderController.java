package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;
import com.hust.coffeeshop.coffeeshopproject.service.PurchaseOrderService;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailRequestDTO; // Import đúng DTO request mới
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(
            @RequestParam Long supplierId, // SỬA TẠI ĐÂY
            @RequestBody List<PurchaseOrderDetailRequestDTO> details) {
        try {
            PurchaseOrder purchaseOrder = purchaseOrderService.createPurchaseOrder(supplierId, details);
            PurchaseOrderResponseDTO responseDTO = purchaseOrderService.convertToPurchaseOrderDTO(purchaseOrder);

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error creating purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrderStatus(
            @PathVariable Long id, // SỬA TẠI ĐÂY
            @RequestParam String status) {
        try {
            PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(id, status);
            PurchaseOrderResponseDTO responseDTO = purchaseOrderService.convertToPurchaseOrderDTO(updatedOrder);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            System.err.println("Error updating purchase order status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}