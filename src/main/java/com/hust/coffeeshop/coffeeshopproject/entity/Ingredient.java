package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;
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
    private Long ingredientId;

    @Column(name = "ingredientname", length = 100, nullable = false, unique = true)
    private String ingredientName;

    @Column(name = "unitofmeasure", length = 20, nullable = false)
    private String unitOfMeasure;

    @Column(name = "currentstock", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "minstocklevel", precision = 10, scale = 2)
    private BigDecimal minStockLevel = BigDecimal.ZERO;

    @Column(name = "lastrestockdate")
    private LocalDate lastRestockDate;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails;
}