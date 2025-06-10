package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerOrderService;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException; // Thêm import này

import java.util.List;
import java.util.Optional; // Thêm import này

@RestController
@RequestMapping("/api/customerorders")
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createCustomerOrder(@RequestBody CustomerOrderRequestDTO requestDTO) {
        try {
            CustomerOrderResponseDTO createdOrder = customerOrderService.createCustomerOrder(requestDTO);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Ví dụ: CustomerId không tồn tại
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Ví dụ: lỗi ràng buộc dữ liệu
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllCustomerOrders() {
        List<CustomerOrderResponseDTO> orders = customerOrderService.getAllCustomerOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getCustomerOrderById(@PathVariable Integer id) {
        return customerOrderService.getCustomerOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // *** PHƯƠNG THỨC NÀY ĐÃ ĐƯỢC THÊM VÀO ĐỂ KHẮC PHỤC LỖI 404 ***
    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateCustomerOrder(@PathVariable Integer id, @RequestBody CustomerOrderRequestDTO requestDTO) {
        try {
            CustomerOrderResponseDTO updatedOrder = customerOrderService.updateCustomerOrder(id, requestDTO);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Order hoặc các entity liên quan không tìm thấy
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Lỗi dữ liệu trùng lặp, thiếu...
        }
    }
    // ***************************************************************

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerOrder(@PathVariable Integer id) {
        try {
            customerOrderService.deleteCustomerOrder(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Order không tìm thấy để xóa
        }
    }
}