package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "membership_rank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRank {

    @Id
    @Column(name = "rankname")
    private String rankName;

    @Column(name = "pointfrom", precision = 15, scale = 2)
    private BigDecimal pointFrom;

    @Column(name = "pointto", precision = 15, scale = 2)
    private BigDecimal pointTo;

    @Column(name = "discountrate", precision = 5, scale = 2) 
    private BigDecimal discountRate;

    @Column(name = "loyaltypointrate", precision = 5, scale = 2) 
    private BigDecimal loyaltyPointRate;

    @OneToMany(mappedBy = "membershipRank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customer> customers;
}