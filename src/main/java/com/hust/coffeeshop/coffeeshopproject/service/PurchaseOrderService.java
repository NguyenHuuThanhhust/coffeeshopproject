package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrderDetail;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderDetailRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import jakarta.transaction.Transactional; // Import đúng thư viện Transactional
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Thêm import này

// Import các DTO response mới
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailResponseDTO;


@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                SupplierRepository supplierRepository,
                                IngredientRepository ingredientRepository,
                                PurchaseOrderDetailRepository purchaseOrderDetailRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.ingredientRepository = ingredientRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
    }

    // Lớp Request DTO (bạn có thể để nó ở đây hoặc chuyển sang package riêng)
    public static class PurchaseOrderDetailRequest {
        private Integer ingredientId;
        private Double quantityOrdered;
        private BigDecimal unitPrice;

        // Getters and Setters
        public Integer getIngredientId() {
            return ingredientId;
        }

        public void setIngredientId(Integer ingredientId) {
            this.ingredientId = ingredientId;
        }

        public Double getQuantityOrdered() {
            return quantityOrdered;
        }

        public void setQuantityOrdered(Double quantityOrdered) {
            this.quantityOrdered = quantityOrdered;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(Integer supplierId, List<PurchaseOrderDetailRequest> details) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setOrderStatus("Pending"); // Default status
        purchaseOrder.setTotalAmount(BigDecimal.ZERO); // Sẽ tính toán sau

        // Lưu đơn hàng trước để có purchaseOrderId
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderDetail> orderDetails = new ArrayList<>();

        for (PurchaseOrderDetailRequest detailRequest : details) {
            Ingredient ingredient = ingredientRepository.findById(detailRequest.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + detailRequest.getIngredientId()));

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPurchaseOrder(purchaseOrder);
            detail.setIngredient(ingredient);
            detail.setQuantityOrdered(detailRequest.getQuantityOrdered());
            detail.setUnitPrice(detailRequest.getUnitPrice());

            orderDetails.add(detail);
            totalAmount = totalAmount.add(detailRequest.getUnitPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantityOrdered())));
        }

        purchaseOrder.setPurchaseOrderDetails(orderDetails); // Set chi tiết đơn hàng vào đơn hàng chính
        purchaseOrder.setTotalAmount(totalAmount);

        // Lưu chi tiết đơn hàng (Cascade save nếu cấu hình đúng, hoặc lưu từng cái một)
        // Nếu bạn dùng cascade = CascadeType.ALL trên @OneToMany trong PurchaseOrder Entity
        // thì không cần vòng lặp save từng detail. Nếu không, hãy thêm:
        // purchaseOrderDetailRepository.saveAll(orderDetails);

        return purchaseOrderRepository.save(purchaseOrder); // Lưu lại đơn hàng với totalAmount và details
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(Integer id, String status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with ID: " + id));
        purchaseOrder.setOrderStatus(status);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    // FIX LỖI: Phương thức getSupplierByName từ câu hỏi trước
    public Supplier getSupplierByName(String supplierName) {
        // .orElse(null) hoặc .orElseThrow() tùy vào logic bạn muốn
        return supplierRepository.findBySupplierName(supplierName)
                .orElseThrow(() -> new RuntimeException("Supplier not found with name: " + supplierName));
    }


    // --- PHƯƠNG THỨC ÁNH XẠ ENTITY SANG DTO ---

    public PurchaseOrderResponseDTO convertToPurchaseOrderDTO(PurchaseOrder purchaseOrder) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        dto.setOrderDate(purchaseOrder.getOrderDate());
        dto.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
        dto.setActualDeliveryDate(purchaseOrder.getActualDeliveryDate());
        dto.setOrderStatus(purchaseOrder.getOrderStatus());
        dto.setTotalAmount(purchaseOrder.getTotalAmount());

        // Ánh xạ thông tin Supplier
        if (purchaseOrder.getSupplier() != null) {
            dto.setSupplierId(purchaseOrder.getSupplier().getSupplierId());
            dto.setSupplierName(purchaseOrder.getSupplier().getSupplierName());
            dto.setSupplierContactPerson(purchaseOrder.getSupplier().getContactPerson());
            dto.setSupplierEmail(purchaseOrder.getSupplier().getEmail());
            dto.setSupplierPhoneNumber(purchaseOrder.getSupplier().getPhoneNumber());
        }

        // Ánh xạ PurchaseOrderDetails
        if (purchaseOrder.getPurchaseOrderDetails() != null && !purchaseOrder.getPurchaseOrderDetails().isEmpty()) {
            List<PurchaseOrderDetailResponseDTO> detailDTOs = purchaseOrder.getPurchaseOrderDetails().stream()
                    .map(this::convertToPurchaseOrderDetailDTO)
                    .collect(Collectors.toList());
            dto.setOrderDetails(detailDTOs);
        } else {
            dto.setOrderDetails(new ArrayList<>()); // Trả về danh sách rỗng nếu không có chi tiết
        }

        return dto;
    }

    public PurchaseOrderDetailResponseDTO convertToPurchaseOrderDetailDTO(PurchaseOrderDetail detail) {
        PurchaseOrderDetailResponseDTO dto = new PurchaseOrderDetailResponseDTO();
        dto.setQuantityOrdered(detail.getQuantityOrdered());
        dto.setUnitPrice(detail.getUnitPrice());

        // Ánh xạ thông tin Ingredient
        if (detail.getIngredient() != null) {
            dto.setIngredientId(detail.getIngredient().getIngredientId());
            dto.setIngredientName(detail.getIngredient().getIngredientName());
        }
        return dto;
    }
}