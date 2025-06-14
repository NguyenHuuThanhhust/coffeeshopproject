package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EVENT_PROMOTION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotionid")
    private Long promotionId; 

    @Column(name = "promotionname", nullable = false, length = 100, unique = true)
    private String promotionName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "promotiontype", length = 50) 
    private String promotionType;

    @Column(name = "value", precision = 10, scale = 2) 
    private BigDecimal value;

    @Column(name = "remaininguses")
    private Integer remainingUses = 0; 

    @Column(name = "startdate") 
    private LocalDate startDate;

    @Column(name = "enddate") 
    private LocalDate endDate;

    @Column(name = "minorderamount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO; 
}