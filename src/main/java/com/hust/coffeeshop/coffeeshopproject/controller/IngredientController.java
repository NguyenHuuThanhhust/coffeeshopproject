package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.IngredientRequestDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientResponseDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.service.IngredientService;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Thêm import này

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> createIngredient(@RequestBody IngredientRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        IngredientResponseDTO savedIngredient = ingredientService.createIngredient(requestDTO); // SỬA TẠI ĐÂY
        return new ResponseEntity<>(savedIngredient, HttpStatus.CREATED);
    }

    @GetMapping // MỚI: API để lấy tất cả nguyên liệu
    public ResponseEntity<List<IngredientResponseDTO>> getAllIngredients() {
        List<IngredientResponseDTO> ingredients = ingredientService.getAllIngredients();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("/{id}") // MỚI: API để lấy nguyên liệu theo ID
    public ResponseEntity<IngredientResponseDTO> getIngredientById(@PathVariable Long id) { // SỬA ID TẠI ĐÂY
        return ingredientService.getIngredientById(id)
                .map(ingredientDTO -> new ResponseEntity<>(ingredientDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}") // MỚI: API để cập nhật nguyên liệu
    public ResponseEntity<IngredientResponseDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientRequestDTO requestDTO) { // SỬA ID TẠI ĐÂY
        try {
            IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(id, requestDTO);
            return new ResponseEntity<>(updatedIngredient, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}") // MỚI: API để xóa nguyên liệu
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) { // SỬA ID TẠI ĐÂY
        try {
            ingredientService.deleteIngredient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}