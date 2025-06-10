package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredientid")
    private Integer ingredientId;

    @Column(name = "ingredientname", length = 255, nullable = false, unique = true)
    private String ingredientName;

    @Column(name = "unitofmeasure", length = 50)
    private String unitOfMeasure;

    @Column(name = "currentstock", nullable = false)
    private Double currentStock;

    @Column(name = "minstocklevel")
    private Double minStockLevel;

    @Column(name = "lastrestockdate")
    private LocalDate lastRestockDate;

    // Mối quan hệ hai chiều với PurchaseOrderDetail (nếu có)
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // KHÔNG CÓ ANNOTATION JACKSON Ở ĐÂY
    private List<PurchaseOrderDetail> purchaseOrderDetails;
    // Constructor, getters và setters nếu không dùng Lombok
}