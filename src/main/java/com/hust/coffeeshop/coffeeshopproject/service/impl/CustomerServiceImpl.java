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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService { // <-- Đây là class CustomerServiceImpl

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

    private Customer convertToEntity(CustomerRequestDTO customerDTO) { // Tham số là 'customerDTO'
        Customer customer = new Customer();
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setEmail(customerDTO.getEmail());

        if (customerDTO.getRankName() != null && !customerDTO.getRankName().isEmpty()) {
            MembershipRank membershipRank = membershipRankRepository.findById(customerDTO.getRankName())
                    .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + customerDTO.getRankName() + " not found."));
            customer.setMembershipRank(membershipRank);
        }
        return customer;
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        if (customer.getDateJoined() == null) {
            customer.setDateJoined(LocalDate.now());
        }
        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(BigDecimal.ZERO);
        }
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDto(savedCustomer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerResponseDTO> getCustomerById(Integer id) {
        return customerRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Integer id, CustomerRequestDTO customerDTO) {
        return customerRepository.findById(id).map(existingCustomer -> {
            existingCustomer.setCustomerName(customerDTO.getCustomerName());
            existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
            existingCustomer.setEmail(customerDTO.getEmail());

            if (customerDTO.getRankName() != null && !customerDTO.getRankName().isEmpty()) {
                MembershipRank membershipRank = membershipRankRepository.findById(customerDTO.getRankName())
                        .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + customerDTO.getRankName() + " not found."));
                existingCustomer.setMembershipRank(membershipRank);
            }

            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return convertToDto(updatedCustomer);
        }).orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found."));
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) { // Thêm kiểm tra này để ném lỗi nếu không tìm thấy
            throw new EntityNotFoundException("Customer with ID " + id + " not found.");
        }
        customerRepository.deleteById(id);
    }
}