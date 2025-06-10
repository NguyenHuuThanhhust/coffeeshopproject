package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data; // Nếu bạn dùng Lombok
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "supplier")
@Data // Nếu dùng Lombok
@NoArgsConstructor // Nếu dùng Lombok
@AllArgsConstructor // Nếu dùng Lombok
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplierid")
    private Integer supplierId;

    @Column(name = "suppliername", length = 255, nullable = false, unique = true)
    private String supplierName;

    @Column(name = "contactperson", length = 100) // Correct name
    private String contactPerson;

    @Column(name = "phonenumber", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    // Mối quan hệ hai chiều: KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrder> purchaseOrders;

    // Constructor, getters và setters nếu không dùng Lombok
}