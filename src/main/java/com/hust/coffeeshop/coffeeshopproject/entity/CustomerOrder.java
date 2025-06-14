package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeid")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionid")
    private EventPromotion eventPromotion;

    @Column(name = "ordertime")
    private LocalDateTime orderTime;

    @Column(name = "expectedpickuptime")
    private LocalDateTime expectedPickupTime;

    @Column(name = "totalamount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "totalamountusd", precision = 10, scale = 2)
    private BigDecimal totalAmountUsd;

    @Column(name = "exchangerate", precision = 10, scale = 4)
    private BigDecimal exchangeRate;

    @Column(name = "rankdiscount", precision = 10, scale = 2)
    private BigDecimal rankDiscount;

    @Column(name = "promotiondiscount", precision = 10, scale = 2)
    private BigDecimal promotionDiscount;

    @Column(name = "orderstatus", length = 30)
    private String orderStatus;

    @Column(name = "paymentmethod", length = 50)
    private String paymentMethod;

    @Column(name = "ispaid")
    private Boolean isPaid;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}