package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier; // Vẫn cần import Entity nếu service trả về Entity
import com.hust.coffeeshop.coffeeshopproject.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<SupplierResponseDTO> responseDTOs = supplierService.convertToSupplierDTOs(suppliers);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Integer id) {
        // Sử dụng phương thức getSupplierById() của service
        return supplierService.getSupplierById(id) // <-- Sửa đổi ở đây
                .map(supplierService::convertToSupplierDTO) // Ánh xạ sang DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // API lấy Supplier theo tên (trả về DTO nếu muốn)
    @GetMapping("/name/{name}") // <-- API mới theo hình ảnh bạn cung cấp
    public ResponseEntity<SupplierResponseDTO> getSupplierByName(@PathVariable String name) {
        try {
            // SupplierService.getSupplierByName() trả về Supplier trực tiếp (đã xử lý Optional bên trong service)
            Supplier supplier = supplierService.getSupplierByName(name);
            SupplierResponseDTO responseDTO = supplierService.convertToSupplierDTO(supplier);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            // Log lỗi để dễ debug hơn
            System.err.println("Error fetching supplier by name: " + e.getMessage());
            return ResponseEntity.notFound().build(); // Hoặc trả về lỗi cụ thể hơn
        }
    }


    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody Supplier supplier) {
        try {
            Supplier createdSupplier = supplierService.createSupplier(supplier);
            SupplierResponseDTO responseDTO = supplierService.convertToSupplierDTO(createdSupplier); // Ánh xạ sang DTO
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error creating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(@PathVariable Integer id, @RequestBody Supplier supplier) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
            SupplierResponseDTO responseDTO = supplierService.convertToSupplierDTO(updatedSupplier); // Ánh xạ sang DTO
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Integer id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}