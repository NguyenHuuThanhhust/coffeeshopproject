package com.hust.coffeeshop.coffeeshopproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuitemid")
    private Long menuItemId;

    @Column(name = "itemname", nullable = false, length = 100)
    private String itemName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "isavailable")
    private Boolean isAvailable;

    @Column(name = "imageurl", length = 255)
    private String imageUrl;

    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "minsellingprice", precision = 10, scale = 2)
    private BigDecimal minSellingPrice;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}