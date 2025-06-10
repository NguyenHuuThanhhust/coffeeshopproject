package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    CustomerOrderResponseDTO createCustomerOrder(CustomerOrderRequestDTO requestDTO);
    List<CustomerOrderResponseDTO> getAllCustomerOrders();
    Optional<CustomerOrderResponseDTO> getCustomerOrderById(Integer id);
    CustomerOrderResponseDTO updateCustomerOrder(Integer id, CustomerOrderRequestDTO requestDTO);
    void deleteCustomerOrder(Integer id);
}