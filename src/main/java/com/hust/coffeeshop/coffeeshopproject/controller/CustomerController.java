package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerDTO) {
        try {
            CustomerResponseDTO createdCustomer = customerService.createCustomer(customerDTO);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerResponseDTO> searchCustomerByPhoneNumber(@RequestParam String phoneNumber) {
        return customerService.findCustomerByPhoneNumber(phoneNumber)
                .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequestDTO customerDTO,
                                                              @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id,
                                               @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            customerService.deleteCustomer(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}