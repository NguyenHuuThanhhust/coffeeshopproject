package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data; // Nếu bạn dùng Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "purchase_order")
@Data // Nếu dùng Lombok
@NoArgsConstructor // Nếu dùng Lombok
@AllArgsConstructor // Nếu dùng Lombok
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchaseorderid")
    private Integer purchaseOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierid", nullable = false)
    // KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    private Supplier supplier;

    @Column(name = "orderdate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expecteddeliverydate")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actualdeliverydate")
    private LocalDate actualDeliveryDate;

    @Column(name = "orderstatus", length = 50, nullable = false)
    private String orderStatus;

    @Column(name = "totalamount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Mối quan hệ hai chiều: KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails;

    // Constructor, getters và setters nếu không dùng Lombok
}