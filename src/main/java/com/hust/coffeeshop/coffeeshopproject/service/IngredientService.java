package com.hust.coffeeshop.coffeeshopproject.service;
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IngredientService { 
    List<IngredientResponseDTO> getAllIngredients();
    Optional<IngredientResponseDTO> getIngredientById(Long id);
    IngredientResponseDTO createIngredient(IngredientRequestDTO requestDTO);
    IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO requestDTO);
    void deleteIngredient(Long id);
    IngredientResponseDTO updateStock(Long id, BigDecimal quantityChange); 
    Optional<IngredientResponseDTO> getIngredientByName(String name); 
}