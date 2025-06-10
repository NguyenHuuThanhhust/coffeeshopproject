package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data; // Nếu bạn dùng Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_detail")
@Data // Nếu dùng Lombok
@NoArgsConstructor // Nếu dùng Lombok
@AllArgsConstructor // Nếu dùng Lombok
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchaseorderdetailid")
    private Integer purchaseOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaseorderid", nullable = false)
    // KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientid", nullable = false)
    // KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    private Ingredient ingredient;

    @Column(name = "quantityordered", nullable = false)
    private Double quantityOrdered;

    @Column(name = "unitprice", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    // Constructor, getters và setters nếu không dùng Lombok
}