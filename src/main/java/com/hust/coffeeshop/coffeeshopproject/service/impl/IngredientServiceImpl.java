package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.IngredientRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import com.hust.coffeeshop.coffeeshopproject.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IngredientServiceImpl implements IngredientService { 
    private static final Logger logger = LoggerFactory.getLogger(IngredientServiceImpl.class);

    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public IngredientResponseDTO convertToDto(Ingredient ingredient) {
        if (ingredient == null) return null;
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setIngredientId(ingredient.getIngredientId()); 
        dto.setIngredientName(ingredient.getIngredientName());
        dto.setUnitOfMeasure(ingredient.getUnitOfMeasure());
        dto.setCurrentStock(ingredient.getCurrentStock()); 
        dto.setMinStockLevel(ingredient.getMinStockLevel());
        dto.setLastRestockDate(ingredient.getLastRestockDate());
        return dto;
    }

    private Ingredient convertToEntity(IngredientRequestDTO dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(dto.getIngredientName());
        ingredient.setUnitOfMeasure(dto.getUnitOfMeasure());
        ingredient.setCurrentStock(dto.getCurrentStock()); 
        ingredient.setMinStockLevel(dto.getMinStockLevel()); 
        return ingredient;
    }

    @Override
    @Transactional
    public IngredientResponseDTO createIngredient(IngredientRequestDTO requestDTO) {
        logger.info("Attempting to create ingredient with DTO: {}", requestDTO);
        try {
            if (ingredientRepository.findByIngredientName(requestDTO.getIngredientName()).isPresent()) {
                throw new IllegalArgumentException("Ingredient with name '" + requestDTO.getIngredientName() + "' already exists.");
            }
            Ingredient ingredient = convertToEntity(requestDTO);
            ingredient.setLastRestockDate(LocalDate.now());
            Ingredient savedIngredient = ingredientRepository.save(ingredient);
            logger.info("Successfully created ingredient with ID: {}", savedIngredient.getIngredientId());
            return convertToDto(savedIngredient);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during ingredient creation: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during ingredient creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during ingredient creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating ingredient: " + e.getMessage(), e);
        }
    }

    @Override
    public List<IngredientResponseDTO> getAllIngredients() {
        logger.info("Fetching all ingredients.");
        return ingredientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IngredientResponseDTO> getIngredientById(Long id) { 
        logger.info("Fetching ingredient with ID: {}", id);
        return ingredientRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public Optional<IngredientResponseDTO> getIngredientByName(String name) {
        logger.info("Fetching ingredient by name: {}", name);
        return ingredientRepository.findByIngredientName(name).map(this::convertToDto);
    }

    @Override
    @Transactional
    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO requestDTO) { 
        logger.info("Attempting to update ingredient with ID: {}, DTO: {}", id, requestDTO);
        try {
            return ingredientRepository.findById(id).map(existingIngredient -> { 
                existingIngredient.setIngredientName(requestDTO.getIngredientName());
                existingIngredient.setUnitOfMeasure(requestDTO.getUnitOfMeasure());
                existingIngredient.setCurrentStock(requestDTO.getCurrentStock()); 
                existingIngredient.setMinStockLevel(requestDTO.getMinStockLevel()); 
                Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);
                logger.info("Successfully updated ingredient with ID: {}", updatedIngredient.getIngredientId());
                return convertToDto(updatedIngredient);
            }).orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during ingredient update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during ingredient update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during ingredient update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating ingredient: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteIngredient(Long id) { 
        logger.info("Attempting to delete ingredient with ID: {}", id);
        try {
            if (!ingredientRepository.existsById(id)) { 
                throw new EntityNotFoundException("Ingredient with ID " + id + " not found.");
            }
            ingredientRepository.deleteById(id); 
            logger.info("Ingredient with ID {} deleted successfully.", id);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during ingredient deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during ingredient deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting ingredient: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public IngredientResponseDTO updateStock(Long id, BigDecimal quantityChange) {
        logger.info("Attempting to update stock for ingredient ID: {} with change: {}", id, quantityChange);
        try {
            return ingredientRepository.findById(id).map(existingIngredient -> { 
                BigDecimal newStock = existingIngredient.getCurrentStock().add(quantityChange);
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Current stock cannot be negative. Attempted change would result in: " + newStock);
                }
                existingIngredient.setCurrentStock(newStock);
                existingIngredient.setLastRestockDate(LocalDate.now());
                Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);
                logger.info("Successfully updated stock for ingredient ID {}. New stock: {}", id, updatedIngredient.getCurrentStock());
                return convertToDto(updatedIngredient);
            }).orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + id + " not found."));
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during stock update: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during stock update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during stock update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating stock: " + e.getMessage(), e);
        }
    }
}