package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderdetailid")
    private Long orderDetailId; // SỬA TẠI ĐÂY: Integer -> Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid", nullable = false)
    private CustomerOrder customerOrder; // CustomerOrder.orderId là Long

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuitemid", nullable = false)
    private MenuItem menuItem; // MenuItem.menuItemId là Long

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unitprice", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}