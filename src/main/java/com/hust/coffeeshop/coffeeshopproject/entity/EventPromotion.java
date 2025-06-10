// src/main/java/com/hust/coffeeshop/coffeeshopproject/entity/EventPromotion.java
package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate; // Sử dụng LocalDate vì cột DB là DATE

@Entity
@Table(name = "EVENT_PROMOTION") // Tên bảng trong DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotionid") // Khớp với tên cột trong DB (viết thường)
    private Integer promotionId;

    @Column(name = "promotionname", nullable = false, length = 100) // Khớp với tên cột trong DB (viết thường), độ dài 100
    private String promotionName;

    @Column(name = "description", columnDefinition = "TEXT") // Khớp với tên cột trong DB (viết thường)
    private String description;

    @Column(name = "promotiontype", nullable = false, length = 50) // Thêm vào, khớp với tên cột trong DB (viết thường)
    private String promotionType;

    @Column(name = "value", nullable = false, precision = 10, scale = 2) // Khớp với tên cột trong DB (viết thường), precision 10, scale 2
    private BigDecimal value;

    @Column(name = "remaininguses") // Thêm vào, khớp với tên cột trong DB (viết thường)
    private Integer remainingUses;

    @Column(name = "startdate", nullable = false) // Khớp với tên cột trong DB (viết thường)
    private LocalDate startDate;

    @Column(name = "enddate", nullable = false) // Khớp với tên cột trong DB (viết thường)
    private LocalDate endDate;

    @Column(name = "minorderamount") // Thêm vào, khớp với tên cột trong DB (viết thường)
    private BigDecimal minOrderAmount;


}