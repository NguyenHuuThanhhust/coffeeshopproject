package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.SupplierRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
// import com.hust.coffeeshop.coffeeshopproject.entity.Supplier; // Không cần import Entity ở đây
import com.hust.coffeeshop.coffeeshopproject.service.SupplierService;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Thêm import này

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSuppliers() {
        // supplierService.getAllSuppliers() đã trả về List<SupplierResponseDTO>
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers(); // DÒNG 26
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) { // ID phải là Long
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierResponseDTO> getSupplierByName(@PathVariable String name) {
        try {
            // supplierService.getSupplierByName() đã trả về SupplierResponseDTO
            SupplierResponseDTO supplier = supplierService.getSupplierByName(name); // DÒNG 36
            return ResponseEntity.ok(supplier);
        } catch (EntityNotFoundException e) { // Sửa RuntimeException thành EntityNotFoundException
            System.err.println("Error fetching supplier by name: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO requestDTO) {
        try {
            // supplierService.createSupplier() đã trả về SupplierResponseDTO
            SupplierResponseDTO createdSupplier = supplierService.createSupplier(requestDTO); // DÒNG 44
            return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) { // Sửa RuntimeException thành IllegalArgumentException
            System.err.println("Error creating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequestDTO requestDTO) {
        try {
            // supplierService.updateSupplier() đã trả về SupplierResponseDTO
            SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(id, requestDTO); // DÒNG 56
            return ResponseEntity.ok(updatedSupplier);
        } catch (EntityNotFoundException e) { // Sửa RuntimeException thành EntityNotFoundException
            System.err.println("Error updating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) { // Sửa RuntimeException thành EntityNotFoundException
            System.err.println("Error deleting supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}