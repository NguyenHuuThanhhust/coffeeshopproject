package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.DailySalesSummaryDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.ProductSalesPerformanceDTO;
import com.hust.coffeeshop.coffeeshopproject.repository.ReportRepository;
import com.hust.coffeeshop.coffeeshopproject.service.ReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public List<DailySalesSummaryDTO> getDailySalesSummary() {
        List<Object[]> rows = entityManager
                .createNativeQuery("SELECT * FROM DailySalesSummary")
                .getResultList();

        return rows.stream().map(row -> new DailySalesSummaryDTO(
                ((Timestamp) row[0]).toLocalDateTime().toLocalDate(),
                ((Number) row[1]).intValue(),
                (BigDecimal) row[2],
                (BigDecimal) row[3],
                (BigDecimal) row[4],
                (BigDecimal) row[5]
        )).collect(Collectors.toList());
    }

        @Override
    public List<ProductSalesPerformanceDTO> getProductSalesPerformance(){
        List<Object[]> rows = reportRepository.getProductSalesPerformance();
        return rows.stream().map(row -> new ProductSalesPerformanceDTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).intValue(),
                (BigDecimal) row[4],
                (BigDecimal) row[5],
                ((Number) row[6]).intValue()
        )).collect(Collectors.toList());
    }
}
