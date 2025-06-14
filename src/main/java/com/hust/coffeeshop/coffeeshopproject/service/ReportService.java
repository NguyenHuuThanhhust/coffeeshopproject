package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.DailySalesSummaryDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.ProductSalesPerformanceDTO;

import java.util.List;

public interface ReportService {
    List<DailySalesSummaryDTO> getDailySalesSummary();
    List<ProductSalesPerformanceDTO> getProductSalesPerformance();
}


