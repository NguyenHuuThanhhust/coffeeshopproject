package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeid")
    private Long employeeId;

    @Column(name = "employeename", nullable = false, length = 100)
    private String employeeName;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "phonenumber", length = 20)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "startdate")
    private LocalDate startDate;

    @Column(name = "hourlywage", precision = 10, scale = 2)
    private BigDecimal hourlyWage = BigDecimal.ZERO;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrder> customerOrders;
}