package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrder; // Đảm bảo Entity này đã có ID là Long
import com.hust.coffeeshop.coffeeshopproject.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> { // SỬA TẠI ĐÂY: Integer -> Long
    List<PurchaseOrderDetail> findByPurchaseOrder_Supplier_SupplierId(Long supplierId); // SỬA TẠI ĐÂY: Integer -> Long
    // Nếu có các phương thức khác liên quan đến ID, hãy sửa chúng tương tự
}