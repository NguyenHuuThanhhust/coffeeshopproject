package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.IngredientRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.IngredientResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> createIngredient(@RequestBody IngredientRequestDTO requestDTO,
                                                                  @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            IngredientResponseDTO savedIngredient = ingredientService.createIngredient(requestDTO);
            return new ResponseEntity<>(savedIngredient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDTO>> getAllIngredients() {
        List<IngredientResponseDTO> ingredients = ingredientService.getAllIngredients();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id)
                .map(ingredientDTO -> new ResponseEntity<>(ingredientDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<IngredientResponseDTO> getIngredientByName(@PathVariable String name) {
        return ingredientService.getIngredientByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<IngredientResponseDTO> getIngredientByNameQuery(@RequestParam String name) {
        return ingredientService.getIngredientByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientRequestDTO requestDTO,
                                                                  @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(id, requestDTO);
            return new ResponseEntity<>(updatedIngredient, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id,
                                                 @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            ingredientService.deleteIngredient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<IngredientResponseDTO> updateIngredientStock(@PathVariable Long id,
                                                                       @RequestParam BigDecimal quantityChange,
                                                                       @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            IngredientResponseDTO updatedIngredient = ingredientService.updateStock(id, quantityChange);
            return ResponseEntity.ok(updatedIngredient);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}