package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailRequestDTO; // MỚI: DTO request cho chi tiết đơn hàng
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrderDetail;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderDetailRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Sửa từ jakarta.transaction.Transactional
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Lớp Request DTO đã được chuyển ra ngoài package riêng: PurchaseOrderDetailRequestDTO.java
    // Xóa lớp lồng này khỏi đây.

    @Transactional
    public PurchaseOrder createPurchaseOrder(Long supplierId, List<PurchaseOrderDetailRequestDTO> details) { // SỬA supplierId thành Long
        Supplier supplier = supplierRepository.findById(supplierId) // SỬA supplierId thành Long
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + supplierId)); // SỬA: RuntimeException -> EntityNotFoundException

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setOrderStatus("Pending");
        purchaseOrder.setTotalAmount(BigDecimal.ZERO);

        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderDetail> orderDetails = new ArrayList<>();

        for (PurchaseOrderDetailRequestDTO detailRequest : details) { // SỬA DTO type
            Ingredient ingredient = ingredientRepository.findById(detailRequest.getIngredientId()) // SỬA: getIngredientId() tham số là Long
                    .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with ID: " + detailRequest.getIngredientId())); // SỬA: RuntimeException -> EntityNotFoundException

            // Kiểm tra quantity
            if (detailRequest.getQuantityOrdered() == null || detailRequest.getQuantityOrdered().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity ordered for Ingredient ID " + detailRequest.getIngredientId() + " must be a positive number.");
            }

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPurchaseOrder(purchaseOrder);
            detail.setIngredient(ingredient);
            detail.setQuantityOrdered(detailRequest.getQuantityOrdered()); // SỬA: Đã là BigDecimal từ DTO
            detail.setUnitPrice(detailRequest.getUnitPrice());

            orderDetails.add(detail);
            totalAmount = totalAmount.add(detailRequest.getUnitPrice().multiply(detailRequest.getQuantityOrdered())); // SỬA: Nhân BigDecimal với BigDecimal
        }

        purchaseOrder.setPurchaseOrderDetails(orderDetails);
        purchaseOrder.setTotalAmount(totalAmount);

        // purchaseOrderDetailRepository.saveAll(orderDetails); // Không cần nếu đã có cascade.ALL

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(Long id, String status) { // SỬA TẠI ĐÂY: Integer -> Long
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id) // SỬA TẠI ĐÂY
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with ID: " + id)); // SỬA: RuntimeException -> EntityNotFoundException
        purchaseOrder.setOrderStatus(status);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    // FIX LỖI: Phương thức getSupplierByName từ câu hỏi trước
    public Supplier getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierName(supplierName)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with name: " + supplierName)); // SỬA: RuntimeException -> EntityNotFoundException
    }

    // MỚI: Thêm getAllPurchaseOrders
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::convertToPurchaseOrderDTO)
                .collect(Collectors.toList());
    }

    // MỚI: Thêm getPurchaseOrderById
    public Optional<PurchaseOrderResponseDTO> getPurchaseOrderById(Long id) { // SỬA: Integer -> Long
        return purchaseOrderRepository.findById(id)
                .map(this::convertToPurchaseOrderDTO);
    }

    // MỚI: Thêm updatePurchaseOrder
    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrder(Long id, PurchaseOrderRequestDTO requestDTO) { // SỬA: Integer -> Long
        return purchaseOrderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setOrderDate(requestDTO.getOrderDate() != null ? requestDTO.getOrderDate() : existingOrder.getOrderDate());
                    existingOrder.setExpectedDeliveryDate(requestDTO.getExpectedDeliveryDate());
                    existingOrder.setActualDeliveryDate(requestDTO.getActualDeliveryDate());
                    existingOrder.setOrderStatus(requestDTO.getOrderStatus() != null ? requestDTO.getOrderStatus() : existingOrder.getOrderStatus());
                    existingOrder.setTotalAmount(requestDTO.getTotalAmount() != null ? requestDTO.getTotalAmount() : existingOrder.getTotalAmount());

                    // Cập nhật chi tiết đơn hàng nhập kho
                    if (requestDTO.getPurchaseOrderDetails() != null) {
                        // Xóa các chi tiết cũ
                        if (existingOrder.getPurchaseOrderDetails() != null && !existingOrder.getPurchaseOrderDetails().isEmpty()) {
                            purchaseOrderDetailRepository.deleteAll(existingOrder.getPurchaseOrderDetails());
                            existingOrder.getPurchaseOrderDetails().clear();
                        }
                        // Thêm chi tiết mới
                        if (!requestDTO.getPurchaseOrderDetails().isEmpty()) {
                            List<PurchaseOrderDetail> newDetails = new ArrayList<>();
                            BigDecimal newTotalAmount = BigDecimal.ZERO;
                            for (PurchaseOrderDetailRequestDTO detailDTO : requestDTO.getPurchaseOrderDetails()) {
                                Ingredient ingredient = ingredientRepository.findById(detailDTO.getIngredientId())
                                        .orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + detailDTO.getIngredientId() + " not found."));

                                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                                detail.setPurchaseOrder(existingOrder);
                                detail.setIngredient(ingredient);
                                detail.setQuantityOrdered(detailDTO.getQuantityOrdered());
                                detail.setUnitPrice(detailDTO.getUnitPrice());
                                newDetails.add(detail);
                                newTotalAmount = newTotalAmount.add(detailDTO.getUnitPrice().multiply(detailDTO.getQuantityOrdered()));
                            }
                            purchaseOrderDetailRepository.saveAll(newDetails);
                            existingOrder.getPurchaseOrderDetails().addAll(newDetails);
                            existingOrder.setTotalAmount(newTotalAmount); // Cập nhật lại tổng tiền
                        } else {
                            existingOrder.setTotalAmount(BigDecimal.ZERO); // Nếu không có chi tiết, tổng tiền là 0
                        }
                    }

                    PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);
                    return convertToPurchaseOrderDTO(updatedOrder);
                }).orElseThrow(() -> new EntityNotFoundException("Purchase Order with ID " + id + " not found."));
    }

    // MỚI: Thêm deletePurchaseOrder
    @Transactional
    public void deletePurchaseOrder(Long id) { // SỬA: Integer -> Long
        if (!purchaseOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Purchase Order with ID " + id + " not found.");
        }
        purchaseOrderRepository.deleteById(id);
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

        if (purchaseOrder.getSupplier() != null) {
            dto.setSupplierId(purchaseOrder.getSupplier().getSupplierId());
            dto.setSupplierName(purchaseOrder.getSupplier().getSupplierName());
            dto.setSupplierContactPerson(purchaseOrder.getSupplier().getContactPerson());
            dto.setSupplierEmail(purchaseOrder.getSupplier().getEmail());
            dto.setSupplierPhoneNumber(purchaseOrder.getSupplier().getPhoneNumber());
        }

        if (purchaseOrder.getPurchaseOrderDetails() != null && !purchaseOrder.getPurchaseOrderDetails().isEmpty()) {
            List<PurchaseOrderDetailResponseDTO> detailDTOs = purchaseOrder.getPurchaseOrderDetails().stream()
                    .map(this::convertToPurchaseOrderDetailDTO)
                    .collect(Collectors.toList());
            dto.setOrderDetails(detailDTOs);
        } else {
            dto.setOrderDetails(new ArrayList<>());
        }
        return dto;
    }

    public PurchaseOrderDetailResponseDTO convertToPurchaseOrderDetailDTO(PurchaseOrderDetail detail) {
        PurchaseOrderDetailResponseDTO dto = new PurchaseOrderDetailResponseDTO();
        // Không cần OrderDetailId ở đây để tránh vòng lặp
        // dto.setPurchaseOrderDetailId(detail.getPurchaseOrderDetailId());
        dto.setQuantityOrdered(detail.getQuantityOrdered());
        dto.setUnitPrice(detail.getUnitPrice());

        if (detail.getIngredient() != null) {
            dto.setIngredientId(detail.getIngredient().getIngredientId());
            dto.setIngredientName(detail.getIngredient().getIngredientName());
        }
        return dto;
    }
}