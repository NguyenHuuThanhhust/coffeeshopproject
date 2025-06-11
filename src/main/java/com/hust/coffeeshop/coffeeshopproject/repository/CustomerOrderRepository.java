package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> { // SỬA TẠI ĐÂY: Integer -> Long
    List<CustomerOrder> findByCustomer_CustomerId(Long customerId); // SỬA TẠI ĐÂY: Integer -> Long
}