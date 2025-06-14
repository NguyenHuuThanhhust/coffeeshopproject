package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.DailySalesSummaryDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.ProductSalesPerformanceDTO;
import com.hust.coffeeshop.coffeeshopproject.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily-sales")
    public List<DailySalesSummaryDTO> getDailySalesSummary() {
        return reportService.getDailySalesSummary();
    }

    @GetMapping("/product-performance")
    public List<ProductSalesPerformanceDTO> getProductSalesPerformance() {
        return reportService.getProductSalesPerformance();
    }
}
