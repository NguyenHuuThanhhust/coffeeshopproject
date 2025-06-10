// src/main/java/com/hust/coffeeshop/coffeeshopproject/controller/EventPromotionController.java
package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.EventPromotionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class EventPromotionController {

    @Autowired
    private EventPromotionService eventPromotionService;

    // CREATE
    @PostMapping
    public ResponseEntity<EventPromotionResponseDTO> createPromotion(@RequestBody EventPromotionRequestDTO requestDTO) {
        EventPromotionResponseDTO createdPromotion = eventPromotionService.createPromotion(requestDTO);
        return new ResponseEntity<>(createdPromotion, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<EventPromotionResponseDTO>> getAllPromotions() {
        List<EventPromotionResponseDTO> promotions = eventPromotionService.getAllPromotions();
        return new ResponseEntity<>(promotions, HttpStatus.OK);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EventPromotionResponseDTO> getPromotionById(@PathVariable Integer id) {
        return eventPromotionService.getPromotionById(id)
                .map(promotionDTO -> new ResponseEntity<>(promotionDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EventPromotionResponseDTO> updatePromotion(@PathVariable Integer id, @RequestBody EventPromotionRequestDTO requestDTO) {
        try {
            EventPromotionResponseDTO updatedPromotion = eventPromotionService.updatePromotion(id, requestDTO);
            return new ResponseEntity<>(updatedPromotion, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        try {
            eventPromotionService.deletePromotion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}