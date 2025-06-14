package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.service.EventPromotionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;


@RestController
@RequestMapping("/api/promotions")
public class EventPromotionController {

    @Autowired
    private EventPromotionService eventPromotionService;

    @PostMapping
    public ResponseEntity<EventPromotionResponseDTO> createPromotion(@RequestBody EventPromotionRequestDTO requestDTO,
                                                                     @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            EventPromotionResponseDTO createdPromotion = eventPromotionService.createPromotion(requestDTO);
            return new ResponseEntity<>(createdPromotion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<EventPromotionResponseDTO>> getAllPromotions() {
        List<EventPromotionResponseDTO> promotions = eventPromotionService.getAllPromotions();
        return new ResponseEntity<>(promotions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventPromotionResponseDTO> getPromotionById(@PathVariable Long id) {
        return eventPromotionService.getPromotionById(id)
                .map(promotionDTO -> new ResponseEntity<>(promotionDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventPromotionResponseDTO> updatePromotion(@PathVariable Long id, @RequestBody EventPromotionRequestDTO requestDTO,
                                                                     @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            EventPromotionResponseDTO updatedPromotion = eventPromotionService.updatePromotion(id, requestDTO);
            return new ResponseEntity<>(updatedPromotion, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id,
                                                @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        if (!"Manager".equalsIgnoreCase(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            eventPromotionService.deletePromotion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}