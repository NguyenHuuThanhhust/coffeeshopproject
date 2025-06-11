package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.SupplierRequestDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // --- Helper methods to convert Entity <-> DTO ---
    public SupplierResponseDTO convertToSupplierDTO(Supplier supplier) {
        if (supplier == null) return null;
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

    private Supplier convertToEntity(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhoneNumber(dto.getPhoneNumber());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        return supplier;
    }

    // --- CRUD Operations ---

    public List<SupplierResponseDTO> getAllSuppliers() { // SỬA KIEU TRA VE
        return supplierRepository.findAll().stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }

    public Optional<SupplierResponseDTO> getSupplierById(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        return supplierRepository.findById(id)
                .map(this::convertToSupplierDTO);
    }

    public SupplierResponseDTO getSupplierByName(String supplierName) { // SỬA KIEU TRA VE
        return supplierRepository.findBySupplierName(supplierName)
                .map(this::convertToSupplierDTO)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with name: " + supplierName));
    }

    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        if (supplierRepository.findBySupplierName(requestDTO.getSupplierName()).isPresent()) {
            throw new IllegalArgumentException("Supplier with name '" + requestDTO.getSupplierName() + "' already exists."); // SỬA: RuntimeException -> IllegalArgumentException
        }
        Supplier supplier = convertToEntity(requestDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToSupplierDTO(savedSupplier);
    }

    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        return supplierRepository.findById(id)
                .map(existingSupplier -> {
                    existingSupplier.setSupplierName(requestDTO.getSupplierName());
                    existingSupplier.setContactPerson(requestDTO.getContactPerson());
                    existingSupplier.setPhoneNumber(requestDTO.getPhoneNumber());
                    existingSupplier.setEmail(requestDTO.getEmail());
                    existingSupplier.setAddress(requestDTO.getAddress());
                    Supplier updatedSupplier = supplierRepository.save(existingSupplier);
                    return convertToSupplierDTO(updatedSupplier);
                })
                .orElseThrow(() -> new EntityNotFoundException("Supplier with ID " + id + " not found.")); // SỬA: RuntimeException -> EntityNotFoundException
    }

    @Transactional
    public void deleteSupplier(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier not found with ID: " + id); // SỬA: RuntimeException -> EntityNotFoundException
        }
        supplierRepository.deleteById(id);
    }
}