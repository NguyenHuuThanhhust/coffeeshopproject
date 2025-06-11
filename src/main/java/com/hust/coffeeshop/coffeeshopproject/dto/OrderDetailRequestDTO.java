package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor
import lombok.AllArgsConstructor; // Thêm AllArgsConstructor
import java.math.BigDecimal;

@Data
@NoArgsConstructor // Thêm
@AllArgsConstructor // Thêm
public class OrderDetailRequestDTO {
    private Long menuItemId; // Đã sửa từ Integer sang Long
    private Integer quantity;
    private BigDecimal unitPrice; // Mặc dù trigger sẽ set, nhưng để giữ sự nhất quán trong DTO
}