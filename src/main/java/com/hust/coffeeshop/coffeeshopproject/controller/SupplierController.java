package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.SupplierRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierResponseDTO> getSupplierByName(@PathVariable String name) {
        return supplierService.getSupplierByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO requestDTO,
                                                              @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            SupplierResponseDTO createdSupplier = supplierService.createSupplier(requestDTO);
            return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during supplier creation: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequestDTO requestDTO,
                                                              @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(id, requestDTO);
            return ResponseEntity.ok(updatedSupplier);
        } catch (EntityNotFoundException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during supplier update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id,
                                               @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during supplier deletion: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}