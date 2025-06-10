package com.hust.coffeeshop.coffeeshopproject.controller;

// import Entity này không còn cần thiết cho kiểu trả về
// import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO; // Import DTO response mới
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder; // Vẫn cần import Entity nếu service trả về Entity
import com.hust.coffeeshop.coffeeshopproject.service.PurchaseOrderService;
import com.hust.coffeeshop.coffeeshopproject.service.PurchaseOrderService.PurchaseOrderDetailRequest; // Vẫn cần cho request body
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
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder( // <-- Thay đổi kiểu trả về sang DTO
                                                                         @RequestParam Integer supplierId,
                                                                         @RequestBody List<PurchaseOrderDetailRequest> details) {
        try {
            // Service vẫn trả về Entity, sau đó Controller sẽ chuyển đổi nó thành DTO
            PurchaseOrder purchaseOrder = purchaseOrderService.createPurchaseOrder(supplierId, details);
            PurchaseOrderResponseDTO responseDTO = purchaseOrderService.convertToPurchaseOrderDTO(purchaseOrder); // <-- Ánh xạ sang DTO

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED); // <-- Trả về DTO
        } catch (RuntimeException e) {
            // Nên log lỗi chi tiết hơn trong môi trường thực tế
            System.err.println("Error creating purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrderStatus( // <-- Cũng thay đổi kiểu trả về ở đây
                                                                               @PathVariable Integer id,
                                                                               @RequestParam String status) {
        try {
            PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(id, status);
            PurchaseOrderResponseDTO responseDTO = purchaseOrderService.convertToPurchaseOrderDTO(updatedOrder); // <-- Ánh xạ sang DTO
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            System.err.println("Error updating purchase order status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}