package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    CustomerOrderResponseDTO createCustomerOrder(CustomerOrderRequestDTO requestDTO);
    List<CustomerOrderResponseDTO> getAllCustomerOrders();
    Optional<CustomerOrderResponseDTO> getCustomerOrderById(Long id); // SỬA TẠI ĐÂY: Integer -> Long
    CustomerOrderResponseDTO updateCustomerOrder(Long id, CustomerOrderRequestDTO requestDTO); // SỬA TẠI ĐÂY: Integer -> Long
    void deleteCustomerOrder(Long id); // SỬA TẠI ĐÂY: Integer -> Long
}