package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<CustomerOrder, Long> {

    @Query(value = "SELECT * FROM DailySalesSummary", nativeQuery = true)
    List<Object[]> getDailySalesSummary();

    @Query(value = "SELECT * FROM ProductSalesPerformance", nativeQuery = true)
    List<Object[]> getProductSalesPerformance();
}

