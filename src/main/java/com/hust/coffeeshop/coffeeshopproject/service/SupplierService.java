package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO; // Import DTO
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // --- CRUD Operations (trả về Entity) ---

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Integer id) {
        return supplierRepository.findById(id);
    }

    // Phương thức bạn đã đề cập trước đó
    public Supplier getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierName(supplierName)
                .orElseThrow(() -> new RuntimeException("Supplier not found with name: " + supplierName));
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        // Có thể thêm logic kiểm tra trùng lặp tên supplier ở đây
        if (supplierRepository.findBySupplierName(supplier.getSupplierName()).isPresent()) {
            throw new RuntimeException("Supplier with name '" + supplier.getSupplierName() + "' already exists.");
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Integer id, Supplier updatedSupplier) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        // Cập nhật thông tin
        existingSupplier.setSupplierName(updatedSupplier.getSupplierName());
        existingSupplier.setContactPerson(updatedSupplier.getContactPerson());
        existingSupplier.setPhoneNumber(updatedSupplier.getPhoneNumber());
        existingSupplier.setEmail(updatedSupplier.getEmail());
        existingSupplier.setAddress(updatedSupplier.getAddress());

        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    // --- Phương thức ánh xạ Entity sang DTO ---

    public SupplierResponseDTO convertToSupplierDTO(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhoneNumber(supplier.getPhoneNumber());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        // KHÔNG BAO GỒM danh sách purchaseOrders ở đây để tránh vòng lặp
        return dto;
    }

    public List<SupplierResponseDTO> convertToSupplierDTOs(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }
}