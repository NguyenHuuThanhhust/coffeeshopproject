package com.hust.coffeeshop.coffeeshopproject.service;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier; 
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<SupplierResponseDTO> getAllSuppliers();
    Optional<SupplierResponseDTO> getSupplierById(Long id); 
    Optional<SupplierResponseDTO> getSupplierByName(String supplierName);
    SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO);
    SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO); 
    void deleteSupplier(Long id); 
    SupplierResponseDTO convertToSupplierDTO(Supplier supplier);
}