package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchaseorderdetailid")
    private Long purchaseOrderDetailId; // SỬA TẠI ĐÂY: Integer -> Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseorderid", nullable = false)
    private PurchaseOrder purchaseOrder; // PurchaseOrder.purchaseOrderId là Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientid", nullable = false)
    private Ingredient ingredient; // Ingredient.ingredientId là Long

    @Column(name = "quantityordered", nullable = false, precision = 10, scale = 2) // SỬA TẠI ĐÂY: Double -> BigDecimal
    private BigDecimal quantityOrdered;

    @Column(name = "unitprice", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
}