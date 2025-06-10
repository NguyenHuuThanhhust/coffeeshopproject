package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List; // If you have @OneToMany to OrderDetail

@Entity
@Table(name = "menu_item") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuitemid") // PK is Integer
    private Integer menuItemId;

    @Column(name = "itemname", nullable = false) // <-- Cần phải có annotation này
    private String itemName; // <-- Tên trường trong Java entity

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false) // Ảnh DB cho thấy cột này là 'price'
    private Double price;

    @Column(name = "category")
    private String category;

    @Column(name = "status") // Ảnh DB cho thấy cột này là 'status'
    private String status; // Tên trường trong Java entity

    @Column(name = "isavailable")
    private Boolean isAvailable;


    @Lob // For large objects like images
    @Column(name = "imageurl") // Could be String for URL or byte[] for binary data
    private String imageUrl; // Or byte[] for binary data

    @Column(name = "thumbnail") // Could be String for URL or byte[] for binary data
    private byte[] thumbnail; // Or String for URL

    @Column(name = "minsellingprice", precision = 10, scale = 2) // Correct type and name
    private BigDecimal minSellingPrice;

    // Mối quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails; // Giả định OrderDetail có private MenuItem menuItem;
}