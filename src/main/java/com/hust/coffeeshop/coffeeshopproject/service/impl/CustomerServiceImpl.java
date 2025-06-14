package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Customer;
import com.hust.coffeeshop.coffeeshopproject.entity.MembershipRank;
import com.hust.coffeeshop.coffeeshopproject.repository.CustomerRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.MembershipRankRepository;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MembershipRankRepository membershipRankRepository;

    private CustomerResponseDTO convertToDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail());
        dto.setDateJoined(customer.getDateJoined());
        dto.setTotalSpent(customer.getTotalSpent());
        if (customer.getMembershipRank() != null) {
            dto.setRankName(customer.getMembershipRank().getRankName());
        }
        return dto;
    }

    private Customer mapRequestToEntity(CustomerRequestDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setEmail(customerDTO.getEmail());

        final String effectiveRankName;
        String requestedRankName = customerDTO.getRankName();

        if (requestedRankName == null || requestedRankName.trim().isEmpty()) {
            effectiveRankName = "Bronze";
        } else {
            effectiveRankName = requestedRankName.trim();
        }

        MembershipRank membershipRank = membershipRankRepository.findById(effectiveRankName)
                .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + effectiveRankName + " not found. Please ensure it exists in DB."));
        customer.setMembershipRank(membershipRank);

        customer.setDateJoined(customerDTO.getDateJoined() != null ? customerDTO.getDateJoined() : LocalDate.now());
        customer.setTotalSpent(customerDTO.getTotalSpent() != null ? customerDTO.getTotalSpent() : BigDecimal.ZERO);

        return customer;
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        logger.info("Attempting to create customer with DTO: {}", customerDTO);

        try {
            if (customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isPresent()) {
                logger.warn("Customer with phone number {} already exists. Cannot create duplicate.", customerDTO.getPhoneNumber());
                throw new IllegalArgumentException("Customer with phone number " + customerDTO.getPhoneNumber() + " already exists.");
            }

            Customer customer = mapRequestToEntity(customerDTO);
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Successfully created customer with ID: {}", savedCustomer.getCustomerId());
            return convertToDto(savedCustomer);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during customer creation: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during customer creation: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during customer creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during customer creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating customer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        logger.info("Fetching all customers.");
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerResponseDTO> getCustomerById(Long id) { 
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO) { 
        logger.info("Attempting to update customer with ID: {}, DTO: {}", id, customerDTO);
        try {
            return customerRepository.findById(id).map(existingCustomer -> {
                existingCustomer.setCustomerName(customerDTO.getCustomerName());
                existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
                existingCustomer.setEmail(customerDTO.getEmail());

                if (customerDTO.getRankName() != null && !customerDTO.getRankName().trim().isEmpty()) {
                    final String effectiveRankNameForUpdate = customerDTO.getRankName().trim();
                    MembershipRank membershipRank = membershipRankRepository.findById(effectiveRankNameForUpdate)
                            .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + effectiveRankNameForUpdate + " not found for update."));
                    existingCustomer.setMembershipRank(membershipRank);
                }

                Customer updatedCustomer = customerRepository.save(existingCustomer);
                logger.info("Successfully updated customer with ID: {}", updatedCustomer.getCustomerId());
                return convertToDto(updatedCustomer);
            }).orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found for update."));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during customer update: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during customer update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during customer update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during customer update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating customer: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) { 
        logger.info("Attempting to delete customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer with ID " + id + " not found.");
        }
        customerRepository.deleteById(id);
        logger.info("Customer with ID {} deleted successfully.", id);
    }

    @Override
    public Optional<CustomerResponseDTO> findCustomerByPhoneNumber(String phoneNumber) {
        logger.info("Searching for customer with phone number: {}", phoneNumber);
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(this::convertToDto);
    }
}