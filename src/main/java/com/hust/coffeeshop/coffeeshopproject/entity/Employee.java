package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // If you have @OneToMany to CustomerOrder

@Entity
@Table(name = "employee") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeid") // PK is Integer
    private Integer employeeId;

    @Column(name = "employeename", nullable = false, length = 100) // Correct name
    private String employeeName;

    @Column(name = "position", length = 50) // Correct name
    private String position;

    @Column(name = "phonenumber", length = 20) // Correct name
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100) // Correct name and constraints
    private String email;

    @Column(name = "startdate") // Correct name
    private LocalDate startDate; // DATE type in DB

    @Column(name = "hourlywage", precision = 10, scale = 2) // Correct type and name
    private BigDecimal hourlyWage;

    // Mối quan hệ 1-n với CustomerOrder
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrder> customerOrders; // Giả định CustomerOrder có private Employee employee;
}