package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid")
    private Long customerId; // SỬA TẠI ĐÂY: Integer -> Long

    @Column(name = "customername", nullable = false, length = 100)
    private String customerName;

    @Column(name = "phonenumber", length = 20, unique = true, nullable = false) // Thêm unique, nullable=false
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "datejoined")
    private LocalDate dateJoined = LocalDate.now(); // Set default ở đây hoặc DB

    @Column(name = "totalspent", precision = 15, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO; // Set default ở đây hoặc DB

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank", referencedColumnName = "rankname", nullable = false) // referencedColumnName="rankname"
    private MembershipRank membershipRank;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrder> customerOrders;

    // Constructor thêm để tiện cho việc tạo mới trong CustomerOrderService
    public Customer(Long customerId, String customerName, String phoneNumber, String email, LocalDate dateJoined, BigDecimal totalSpent, String rankName, List<CustomerOrder> customerOrders) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateJoined = (dateJoined != null) ? dateJoined : LocalDate.now();
        this.totalSpent = (totalSpent != null) ? totalSpent : BigDecimal.ZERO;
        // Logic gán MembershipRank cần được handle trong Service hoặc Entity listener
        // Tạm thời, rankName ở đây chỉ là String, không phải MembershipRank entity
        // Bạn cần tải MembershipRank entity dựa trên rankName
        // Ví dụ: this.membershipRank = new MembershipRank(rankName, null, null, null, null, null); // Chỉ là placeholder
        this.customerOrders = customerOrders;
    }
}