package com.hust.coffeeshop.coffeeshopproject.dto;

import com.hust.coffeeshop.coffeeshopproject.dto.PurchaseOrderDetailResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderResponseDTO {
    private Integer purchaseOrderId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String orderStatus;
    private BigDecimal totalAmount;

    // Thay vì đối tượng Supplier đầy đủ, chỉ cần thông tin cần thiết để tránh vòng lặp
    private Integer supplierId;
    private String supplierName;
    private String supplierContactPerson; // Có thể thêm nếu cần
    private String supplierEmail;         // Có thể thêm nếu cần
    private String supplierPhoneNumber;   // Có thể thêm nếu cần

    private List<PurchaseOrderDetailResponseDTO> orderDetails;

    // Constructors, Getters, Setters (có thể dùng Lombok @Data nếu bạn đang dùng)

    public PurchaseOrderResponseDTO() {
    }

    public PurchaseOrderResponseDTO(Integer purchaseOrderId, LocalDate orderDate, LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate, String orderStatus, BigDecimal totalAmount, Integer supplierId, String supplierName, String supplierContactPerson, String supplierEmail, String supplierPhoneNumber, List<PurchaseOrderDetailResponseDTO> orderDetails) {
        this.purchaseOrderId = purchaseOrderId;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierContactPerson = supplierContactPerson;
        this.supplierEmail = supplierEmail;
        this.supplierPhoneNumber = supplierPhoneNumber;
        this.orderDetails = orderDetails;
    }

    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Integer purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierContactPerson() {
        return supplierContactPerson;
    }

    public void setSupplierContactPerson(String supplierContactPerson) {
        this.supplierContactPerson = supplierContactPerson;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getSupplierPhoneNumber() {
        return supplierPhoneNumber;
    }

    public void setSupplierPhoneNumber(String supplierPhoneNumber) {
        this.supplierPhoneNumber = supplierPhoneNumber;
    }

    public List<PurchaseOrderDetailResponseDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<PurchaseOrderDetailResponseDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}