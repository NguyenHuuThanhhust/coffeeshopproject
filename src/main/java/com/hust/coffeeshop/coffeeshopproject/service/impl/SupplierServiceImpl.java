package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.SupplierRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.SupplierResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Supplier;
import com.hust.coffeeshop.coffeeshopproject.repository.SupplierRepository;
import com.hust.coffeeshop.coffeeshopproject.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SupplierServiceImpl implements SupplierService {
    private static final Logger logger = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierResponseDTO convertToSupplierDTO(Supplier supplier) {
        if (supplier == null) return null;
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhoneNumber(supplier.getPhoneNumber());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        return dto;
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

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {
        logger.info("Fetching all suppliers.");
        return supplierRepository.findAll().stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SupplierResponseDTO> getSupplierById(Long id) {
        logger.info("Fetching supplier with ID: {}", id);
        return supplierRepository.findById(id)
                .map(this::convertToSupplierDTO);
    }

    @Override
    public Optional<SupplierResponseDTO> getSupplierByName(String supplierName) {
        logger.info("Fetching supplier by name: {}", supplierName);
        return supplierRepository.findBySupplierName(supplierName)
                .map(this::convertToSupplierDTO);
    }

    @Override
    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO) {
        logger.info("Attempting to create supplier with DTO: {}", requestDTO);
        try {
            if (supplierRepository.findBySupplierName(requestDTO.getSupplierName()).isPresent()) {
                throw new IllegalArgumentException("Supplier with name '" + requestDTO.getSupplierName() + "' already exists.");
            }
            Supplier supplier = convertToEntity(requestDTO);
            Supplier savedSupplier = supplierRepository.save(supplier);
            logger.info("Successfully created supplier with ID: {}", savedSupplier.getSupplierId());
            return convertToSupplierDTO(savedSupplier);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during supplier creation: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during supplier creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during supplier creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating supplier: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO) {
        logger.info("Attempting to update supplier with ID: {}, DTO: {}", id, requestDTO);
        try {
            return supplierRepository.findById(id)
                    .map(existingSupplier -> {
                        existingSupplier.setSupplierName(requestDTO.getSupplierName());
                        existingSupplier.setContactPerson(requestDTO.getContactPerson());
                        existingSupplier.setPhoneNumber(requestDTO.getPhoneNumber());
                        existingSupplier.setEmail(requestDTO.getEmail());
                        existingSupplier.setAddress(requestDTO.getAddress());
                        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
                        logger.info("Successfully updated supplier with ID: {}", updatedSupplier.getSupplierId());
                        return convertToSupplierDTO(updatedSupplier);
                    })
                    .orElseThrow(() -> new EntityNotFoundException("Supplier with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during supplier update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during supplier update: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during supplier update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during supplier update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating supplier: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        logger.info("Attempting to delete supplier with ID: {}", id);
        try {
            if (!supplierRepository.existsById(id)) {
                throw new EntityNotFoundException("Supplier not found with ID: " + id);
            }
            supplierRepository.deleteById(id);
            logger.info("Supplier with ID {} deleted successfully.", id);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during supplier deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during supplier deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting supplier: " + e.getMessage(), e);
        }
    }
}