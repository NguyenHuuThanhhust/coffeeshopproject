package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerOrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RestController
@RequestMapping("/api/customerorders")
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createCustomerOrder(@RequestBody CustomerOrderRequestDTO requestDTO,
                                                                        @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole) && !"Staff".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            CustomerOrderResponseDTO createdOrder = customerOrderService.createCustomerOrder(requestDTO);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllCustomerOrders() {
        List<CustomerOrderResponseDTO> orders = customerOrderService.getAllCustomerOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getCustomerOrderById(@PathVariable Long id) {
        return customerOrderService.getCustomerOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateCustomerOrder(@PathVariable Long id, @RequestBody CustomerOrderRequestDTO requestDTO,
                                                                        @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            CustomerOrderResponseDTO updatedOrder = customerOrderService.updateCustomerOrder(id, requestDTO);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerOrder(@PathVariable Long id,
                                                    @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            customerOrderService.deleteCustomerOrder(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}