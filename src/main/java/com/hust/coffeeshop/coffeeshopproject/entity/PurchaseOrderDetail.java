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
    private Long purchaseOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseorderid", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientid", nullable = false)
    private Ingredient ingredient;

    @Column(name = "quantityordered", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityOrdered;

    @Column(name = "unitprice", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
}