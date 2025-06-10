package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // If you have @OneToMany to CustomerOrder

@Entity
@Table(name = "customer") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid") // PK is Integer
    private Integer customerId;

    @Column(name = "customername", nullable = false, length = 100) // Correct name and constraints
    private String customerName;

    @Column(name = "phonenumber", length = 20) // Correct name
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100) // Correct name and constraints
    private String email;

    @Column(name = "datejoined") // Correct name
    private LocalDate dateJoined; // DATE type in DB

    @Column(name = "totalspent", precision = 15, scale = 2) // Correct type and name
    private BigDecimal totalSpent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank", nullable = false) // FK column name is 'rank' in DB
    private MembershipRank membershipRank; // Matches name in MembershipRank's mappedBy

    // Mối quan hệ 1-n với CustomerOrder
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrder> customerOrders; // Giả định CustomerOrder có private Customer customer;
}