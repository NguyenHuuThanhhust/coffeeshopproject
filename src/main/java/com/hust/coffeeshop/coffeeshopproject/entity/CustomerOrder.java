package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime; // For TIMESTAMP
import java.util.List; // If you have @OneToMany to OrderDetail

@Entity
@Table(name = "customer_order") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid") // PK is Integer
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid", nullable = false) // FK column name is 'customerid'
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeid") // FK column name is 'employeeid'
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionid") // FK column name is 'promotionid'
    private EventPromotion eventPromotion;

    @Column(name = "ordertime") // Correct name
    private LocalDateTime orderTime; // TIMESTAMP type in DB

    @Column(name = "expectedpickuptime") // Correct name
    private LocalDateTime expectedPickupTime; // TIMESTAMP type in DB

    @Column(name = "totalamount", precision = 10, scale = 2) // Correct type and name
    private BigDecimal totalAmount;

    @Column(name = "totalamountusd", precision = 10, scale = 2) // Correct type and name
    private BigDecimal totalAmountUsd;

    @Column(name = "exchangerate", precision = 10, scale = 4) // Correct type and name
    private BigDecimal exchangeRate;

    @Column(name = "rankdiscount", precision = 10, scale = 2) // Correct type and name
    private BigDecimal rankDiscount;

    @Column(name = "promotiondiscount", precision = 10, scale = 2) // Correct type and name
    private BigDecimal promotionDiscount;

    @Column(name = "orderstatus", length = 30) // Correct name
    private String orderStatus;

    @Column(name = "paymentmethod", length = 50) // Correct name
    private String paymentMethod;

    @Column(name = "ispaid") // Correct name
    private Boolean isPaid;

    @Column(name = "notes") // Correct name
    private String notes;

    // Mối quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails; // Giả định OrderDetail có private CustomerOrder customerOrder;
}