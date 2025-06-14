package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerResponseDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO);
    List<CustomerResponseDTO> getAllCustomers();
    Optional<CustomerResponseDTO> getCustomerById(Long id); 
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO); 
    void deleteCustomer(Long id); 
    Optional<CustomerResponseDTO> findCustomerByPhoneNumber(String phoneNumber);
}