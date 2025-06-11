package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.IngredientRequestDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientResponseDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List; // Thêm import này
import java.util.Optional; // Thêm import này
import java.util.stream.Collectors; // Thêm import này

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    // Helper methods to convert Entity <-> DTO
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
        // lastRestockDate thường được DB set default hoặc cập nhật khi nhập kho, không set từ RequestDTO
        return ingredient;
    }

    @Transactional
    public IngredientResponseDTO createIngredient(IngredientRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        // Có thể thêm logic kiểm tra trùng tên nguyên liệu
        if (ingredientRepository.findByIngredientName(requestDTO.getIngredientName()).isPresent()) {
            throw new IllegalArgumentException("Ingredient with name '" + requestDTO.getIngredientName() + "' already exists.");
        }
        Ingredient ingredient = convertToEntity(requestDTO);
        ingredient.setLastRestockDate(LocalDate.now()); // Set ngày tạo
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return convertToDto(savedIngredient);
    }

    public List<IngredientResponseDTO> getAllIngredients() { // MỚI
        return ingredientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<IngredientResponseDTO> getIngredientById(Long id) { // MỚI & SỬA TẠI ĐÂY
        return ingredientRepository.findById(id).map(this::convertToDto);
    }

    @Transactional
    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO requestDTO) { // MỚI & SỬA TẠI ĐÂY
        return ingredientRepository.findById(id).map(existingIngredient -> {
            existingIngredient.setIngredientName(requestDTO.getIngredientName());
            existingIngredient.setUnitOfMeasure(requestDTO.getUnitOfMeasure());
            existingIngredient.setCurrentStock(requestDTO.getCurrentStock());
            existingIngredient.setMinStockLevel(requestDTO.getMinStockLevel());
            // lastRestockDate không được update qua đây
            Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);
            return convertToDto(updatedIngredient);
        }).orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + id + " not found."));
    }

    @Transactional
    public void deleteIngredient(Long id) { // MỚI & SỬA TẠI ĐÂY
        if (!ingredientRepository.existsById(id)) {
            throw new EntityNotFoundException("Ingredient with ID " + id + " not found.");
        }
        ingredientRepository.deleteById(id);
    }
}