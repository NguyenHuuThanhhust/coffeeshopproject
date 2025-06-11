package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Thêm import này

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> { // SỬA TẠI ĐÂY: Integer -> Long

}