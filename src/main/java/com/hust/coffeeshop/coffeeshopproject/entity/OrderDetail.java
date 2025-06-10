package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
// THÊM CÁC DÒNG IMPORT NÀY:
import com.hust.coffeeshop.coffeeshopproject.entity.CustomerOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;

@Entity
@Table(name = "order_detail") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderdetailid") // PK is Integer
    private Integer orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid", nullable = false) // FK column name is 'orderid' in DB
    private CustomerOrder customerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuitemid", nullable = false) // FK column name is 'menuitemid' in DB
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false) // Correct name
    private Integer quantity;

    @Column(name = "unitprice", nullable = false, precision = 10, scale = 2) // Correct type and name
    private BigDecimal unitPrice;
}