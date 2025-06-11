package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> { // SỬA TẠI ĐÂY: Integer -> Long
    List<PurchaseOrder> findBySupplier_SupplierId(Long supplierId); // SỬA TẠI ĐÂY: Integer -> Long
    List<PurchaseOrder> findByOrderStatus(String status);
}