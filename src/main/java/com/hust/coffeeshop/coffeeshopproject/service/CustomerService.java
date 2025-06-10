package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerResponseDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO);

    List<CustomerResponseDTO> getAllCustomers();

    Optional<CustomerResponseDTO> getCustomerById(Integer id);

    CustomerResponseDTO updateCustomer(Integer id, CustomerRequestDTO customerDTO);

    void deleteCustomer(Integer id);
}