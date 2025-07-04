package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> { 
    Optional<Supplier> findBySupplierName(String supplierName);
}