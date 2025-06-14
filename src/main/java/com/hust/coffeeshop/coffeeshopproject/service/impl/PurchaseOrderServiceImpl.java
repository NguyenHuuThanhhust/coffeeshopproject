package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrderDetail;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderDetailRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.PurchaseOrderRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import com.hust.coffeeshop.coffeeshopproject.service.PurchaseOrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService { 
    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                    SupplierRepository supplierRepository,
                                    IngredientRepository ingredientRepository,
                                    PurchaseOrderDetailRepository purchaseOrderDetailRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.ingredientRepository = ingredientRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(Long supplierId, List<PurchaseOrderDetailRequestDTO> details) {        logger.info("Attempting to create purchase order for supplier ID: {} with {} details.", supplierId, details.size());
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + supplierId));

            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setOrderDate(LocalDate.now());
            purchaseOrder.setOrderStatus("Pending");
            purchaseOrder.setTotalAmount(BigDecimal.ZERO);

            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            logger.info("Initial PurchaseOrder saved with ID: {}", purchaseOrder.getPurchaseOrderId());

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<PurchaseOrderDetail> orderDetails = new ArrayList<>();

            for (PurchaseOrderDetailRequestDTO detailRequest : details) {
                Ingredient ingredient = ingredientRepository.findById(detailRequest.getIngredientId())
                        .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with ID: " + detailRequest.getIngredientId()));

                if (detailRequest.getQuantityOrdered() == null || detailRequest.getQuantityOrdered().compareTo(BigDecimal.ZERO) <= 0) { 
                    throw new IllegalArgumentException("Quantity ordered for Ingredient ID " + detailRequest.getIngredientId() + " must be a positive number.");
                }

                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setPurchaseOrder(purchaseOrder);
                detail.setIngredient(ingredient);
                detail.setQuantityOrdered(detailRequest.getQuantityOrdered()); 
                detail.setUnitPrice(detailRequest.getUnitPrice()); 

                orderDetails.add(detail);
                totalAmount = totalAmount.add(detailRequest.getUnitPrice().multiply(detailRequest.getQuantityOrdered())); 
            }

            purchaseOrder.setPurchaseOrderDetails(orderDetails);
            purchaseOrder.setTotalAmount(totalAmount);

            PurchaseOrder finalPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            logger.info("PurchaseOrder final saved with ID: {}, TotalAmount: {}", finalPurchaseOrder.getPurchaseOrderId(), finalPurchaseOrder.getTotalAmount());
            return convertToPurchaseOrderDTO(finalPurchaseOrder);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during purchase order creation: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during purchase order creation: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during purchase order creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during purchase order creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating purchase order: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrderStatus(Long id, String status) { 
        logger.info("Updating purchase order status for ID: {} to status: {}", id, status);
        try {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id) 
                    .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with ID: " + id));
            purchaseOrder.setOrderStatus(status);
            PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
            logger.info("Purchase order ID {} status updated to {}.", id, status);
            return convertToPurchaseOrderDTO(updatedOrder);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during purchase order status update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during purchase order status update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during purchase order status update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating purchase order status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        logger.info("Fetching all purchase orders.");
        return purchaseOrderRepository.findAll().stream()
                .map(this::convertToPurchaseOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PurchaseOrderResponseDTO> getPurchaseOrderById(Long id) { 
        logger.info("Fetching purchase order with ID: {}", id);
        return purchaseOrderRepository.findById(id)
                .map(this::convertToPurchaseOrderDTO);
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrder(Long id, PurchaseOrderRequestDTO requestDTO) { 
        logger.info("Attempting to update purchase order with ID: {}, DTO: {}", id, requestDTO);
        try {
            return purchaseOrderRepository.findById(id) 
                    .map(existingOrder -> {
                        existingOrder.setOrderDate(requestDTO.getOrderDate() != null ? requestDTO.getOrderDate() : existingOrder.getOrderDate());
                        existingOrder.setExpectedDeliveryDate(requestDTO.getExpectedDeliveryDate());
                        existingOrder.setActualDeliveryDate(requestDTO.getActualDeliveryDate());
                        existingOrder.setOrderStatus(requestDTO.getOrderStatus() != null ? requestDTO.getOrderStatus() : existingOrder.getOrderStatus());
                        existingOrder.setTotalAmount(requestDTO.getTotalAmount() != null ? requestDTO.getTotalAmount() : existingOrder.getTotalAmount());

                        if (requestDTO.getPurchaseOrderDetails() != null) {
                            if (existingOrder.getPurchaseOrderDetails() != null && !existingOrder.getPurchaseOrderDetails().isEmpty()) {
                                purchaseOrderDetailRepository.deleteAll(existingOrder.getPurchaseOrderDetails());
                                existingOrder.getPurchaseOrderDetails().clear();
                            }

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
                                existingOrder.setTotalAmount(newTotalAmount);
                            } else {
                                existingOrder.setTotalAmount(BigDecimal.ZERO);
                            }
                        }

                        PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);
                        logger.info("Successfully updated purchase order with ID: {}", updatedOrder.getPurchaseOrderId());
                        return convertToPurchaseOrderDTO(updatedOrder);
                    }).orElseThrow(() -> new EntityNotFoundException("Purchase Order with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during purchase order update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during purchase order update: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during purchase order update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during purchase order update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating purchase order: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(Long id) { 
        logger.info("Attempting to delete purchase order with ID: {}", id);
        try {
            if (!purchaseOrderRepository.existsById(id)) { 
                throw new EntityNotFoundException("Purchase Order with ID " + id + " not found.");
            }
            purchaseOrderRepository.deleteById(id); 
            logger.info("Purchase order with ID {} deleted successfully.", id);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during purchase order deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during purchase order deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting purchase order: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Supplier> getSupplierByName(String supplierName) { 
        logger.info("Fetching supplier by name: {}", supplierName);
        return supplierRepository.findBySupplierName(supplierName);
    }

    @Override
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

    @Override
    public PurchaseOrderDetailResponseDTO convertToPurchaseOrderDetailDTO(PurchaseOrderDetail detail) {
        PurchaseOrderDetailResponseDTO dto = new PurchaseOrderDetailResponseDTO();
        dto.setQuantityOrdered(detail.getQuantityOrdered()); 
        dto.setUnitPrice(detail.getUnitPrice()); 

        if (detail.getIngredient() != null) {
            dto.setIngredientId(detail.getIngredient().getIngredientId()); 
            dto.setIngredientName(detail.getIngredient().getIngredientName());
        }
        return dto;
    }
}