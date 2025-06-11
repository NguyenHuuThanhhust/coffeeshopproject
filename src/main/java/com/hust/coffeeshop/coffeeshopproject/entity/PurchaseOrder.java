package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "purchase_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchaseorderid")
    private Long purchaseOrderId; // SỬA TẠI ĐÂY: Integer -> Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierid", nullable = false)
    private Supplier supplier; // Supplier.supplierId là Long

    @Column(name = "orderdate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expecteddeliverydate")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actualdeliverydate")
    private LocalDate actualDeliveryDate;

    @Column(name = "orderstatus", length = 30, nullable = false) // Thêm length 30
    private String orderStatus;

    @Column(name = "totalamount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails;
}