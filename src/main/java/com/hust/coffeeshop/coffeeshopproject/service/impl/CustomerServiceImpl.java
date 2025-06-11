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

    // Thêm logger nếu bạn chưa có
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MembershipRankRepository membershipRankRepository;

    // Helper method to convert Customer Entity to CustomerResponseDTO
    private CustomerResponseDTO convertToDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId()); // Đảm bảo Customer.customerId là Long
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

    // Helper method to convert CustomerRequestDTO to Customer Entity (for new creation/update)
    private Customer mapRequestToEntity(CustomerRequestDTO customerDTO) {
        Customer customer = new Customer(); // Hoặc tìm existing customer nếu là update
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setEmail(customerDTO.getEmail());

        // SỬA TẠI ĐÂY: Tạo một biến effectively final
        final String effectiveRankName; // Khai báo là final hoặc để IntelliJ tự suy luận effectively final
        String requestedRankName = customerDTO.getRankName();

        if (requestedRankName == null || requestedRankName.trim().isEmpty()) {
            effectiveRankName = "Bronze"; // Gán một lần duy nhất
        } else {
            effectiveRankName = requestedRankName.trim(); // Gán một lần duy nhất
        }

        // Logic gán MembershipRank
        MembershipRank membershipRank = membershipRankRepository.findById(effectiveRankName) // Sử dụng effectiveRankName
                .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + effectiveRankName + " not found. Please ensure it exists in DB."));
        customer.setMembershipRank(membershipRank);

        // Các trường này thường được DB set default, hoặc set ở đây nếu Entity không có default
        // Xóa logic này nếu DB đã có default, hoặc giữ nếu muốn set trong code
        customer.setDateJoined(customerDTO.getDateJoined() != null ? customerDTO.getDateJoined() : LocalDate.now());
        customer.setTotalSpent(customerDTO.getTotalSpent() != null ? customerDTO.getTotalSpent() : BigDecimal.ZERO);

        return customer;
    }


    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        // Log thông tin đầu vào
        logger.info("Attempting to create customer with DTO: {}", customerDTO);

        try {
            // Kiểm tra xem khách hàng đã tồn tại bằng số điện thoại chưa (UNIQUE constraint ở DB cũng sẽ bắt)
            if (customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isPresent()) {
                logger.warn("Customer with phone number {} already exists. Cannot create duplicate.", customerDTO.getPhoneNumber());
                throw new IllegalArgumentException("Customer with phone number " + customerDTO.getPhoneNumber() + " already exists.");
            }

            Customer customer = mapRequestToEntity(customerDTO); // Sử dụng phương thức mapRequestToEntity
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Successfully created customer with ID: {}", savedCustomer.getCustomerId());
            return convertToDto(savedCustomer);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during customer creation: {}", e.getMessage());
            throw e; // Ném lại để Controller bắt (Controller sẽ trả về 400)
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during customer creation: {}", e.getMessage(), e);
            throw e; // Ném lại để Controller bắt (Controller sẽ trả về 409)
        } catch (EntityNotFoundException e) { // Bắt EntityNotFoundException nếu có
            logger.error("Entity not found during customer creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during customer creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating customer: " + e.getMessage(), e); // Ném lại
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
    public Optional<CustomerResponseDTO> getCustomerById(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO) { // SỬA TẠI ĐÂY: Integer -> Long
        logger.info("Attempting to update customer with ID: {}, DTO: {}", id, customerDTO);
        try {
            return customerRepository.findById(id).map(existingCustomer -> {
                existingCustomer.setCustomerName(customerDTO.getCustomerName());
                existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
                existingCustomer.setEmail(customerDTO.getEmail());

                if (customerDTO.getRankName() != null && !customerDTO.getRankName().trim().isEmpty()) {
                    // Cập nhật lại biến effectively final cho rankToUse
                    final String effectiveRankNameForUpdate = customerDTO.getRankName().trim();
                    MembershipRank membershipRank = membershipRankRepository.findById(effectiveRankNameForUpdate)
                            .orElseThrow(() -> new EntityNotFoundException("MembershipRank with name " + effectiveRankNameForUpdate + " not found for update."));
                    existingCustomer.setMembershipRank(membershipRank);
                } else {
                    // Nếu rankName trong DTO là null/empty, bạn có thể quyết định không cập nhật hoặc set về default
                    // Ví dụ: existingCustomer.setMembershipRank(membershipRankRepository.findById("Bronze").orElse(null));
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
    public void deleteCustomer(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
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