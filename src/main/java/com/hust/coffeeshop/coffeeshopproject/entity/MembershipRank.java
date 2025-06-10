package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List; // Nếu bạn có @OneToMany đến Customer

@Entity
@Table(name = "membership_rank") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRank {

    @Id
    @Column(name = "rankname") // PK is varchar(50)
    private String rankName;

    @Column(name = "pointfrom", precision = 15, scale = 2) // Correct type and name
    private BigDecimal pointFrom;

    @Column(name = "pointto", precision = 15, scale = 2) // Correct type and name
    private BigDecimal pointTo;

    @Column(name = "discountrate", precision = 15, scale = 2) // Correct type and name
    private BigDecimal discountRate;

    @Column(name = "loyaltypointrate", precision = 15, scale = 2) // Correct type and name
    private BigDecimal loyaltyPointRate;

    // Mối quan hệ 1-n với Customer (nếu có)
    @OneToMany(mappedBy = "membershipRank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customer> customers; // Giả định Customer có private MembershipRank membershipRank;
}