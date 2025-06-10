package com.hust.coffeeshop.coffeeshopproject.dto;

import java.math.BigDecimal;

public class PurchaseOrderDetailResponseDTO {
    // Không cần ID của PurchaseOrder ở đây để tránh vòng lặp
    // private Integer purchaseOrderId;

    private Integer ingredientId;
    private String ingredientName; // Thêm tên nguyên liệu cho dễ đọc
    private Double quantityOrdered;
    private BigDecimal unitPrice;

    // Constructors, Getters, Setters (có thể dùng Lombok @Data nếu bạn đang dùng)

    public PurchaseOrderDetailResponseDTO() {
    }

    public PurchaseOrderDetailResponseDTO(Integer ingredientId, String ingredientName, Double quantityOrdered, BigDecimal unitPrice) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantityOrdered = quantityOrdered;
        this.unitPrice = unitPrice;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
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