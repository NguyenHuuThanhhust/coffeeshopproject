package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.EventPromotion;
import com.hust.coffeeshop.coffeeshopproject.repository.EventPromotionRepository;
import com.hust.coffeeshop.coffeeshopproject.service.EventPromotionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventPromotionServiceImpl implements EventPromotionService {
    private static final Logger logger = LoggerFactory.getLogger(EventPromotionServiceImpl.class);

    @Autowired
    private EventPromotionRepository eventPromotionRepository;

    private EventPromotionResponseDTO convertToResponseDTO(EventPromotion promotion) {
        EventPromotionResponseDTO dto = new EventPromotionResponseDTO();
        dto.setPromotionId(promotion.getPromotionId()); 
        dto.setPromotionName(promotion.getPromotionName());
        dto.setDescription(promotion.getDescription());
        dto.setPromotionType(promotion.getPromotionType());
        dto.setValue(promotion.getValue());
        dto.setRemainingUses(promotion.getRemainingUses());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setMinOrderAmount(promotion.getMinOrderAmount());
        return dto;
    }

    private EventPromotion convertToEntity(EventPromotionRequestDTO dto) {
        EventPromotion promotion = new EventPromotion();
        promotion.setPromotionName(dto.getPromotionName());
        promotion.setDescription(dto.getDescription());
        promotion.setPromotionType(dto.getPromotionType());
        promotion.setValue(dto.getValue());
        promotion.setRemainingUses(dto.getRemainingUses());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setMinOrderAmount(dto.getMinOrderAmount());
        return promotion;
    }

    @Override
    @Transactional
    public EventPromotionResponseDTO createPromotion(EventPromotionRequestDTO requestDTO) {
        logger.info("Attempting to create promotion with DTO: {}", requestDTO);
        try {
            EventPromotion promotion = convertToEntity(requestDTO);
            EventPromotion savedPromotion = eventPromotionRepository.save(promotion);
            logger.info("Successfully created promotion with ID: {}", savedPromotion.getPromotionId());
            return convertToResponseDTO(savedPromotion);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during promotion creation: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during promotion creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating promotion: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EventPromotionResponseDTO> getAllPromotions() {
        logger.info("Fetching all promotions.");
        return eventPromotionRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EventPromotionResponseDTO> getPromotionById(Long id) { 
        logger.info("Fetching promotion with ID: {}", id);
        return eventPromotionRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional
    public EventPromotionResponseDTO updatePromotion(Long id, EventPromotionRequestDTO requestDTO) { 
        logger.info("Attempting to update promotion with ID: {}, DTO: {}", id, requestDTO);
        try {
            return eventPromotionRepository.findById(id) 
                    .map(existingPromotion -> {
                        existingPromotion.setPromotionName(requestDTO.getPromotionName());
                        existingPromotion.setDescription(requestDTO.getDescription());
                        existingPromotion.setPromotionType(requestDTO.getPromotionType());
                        existingPromotion.setValue(requestDTO.getValue());
                        existingPromotion.setRemainingUses(requestDTO.getRemainingUses());
                        existingPromotion.setStartDate(requestDTO.getStartDate());
                        existingPromotion.setEndDate(requestDTO.getEndDate());
                        existingPromotion.setMinOrderAmount(requestDTO.getMinOrderAmount());

                        EventPromotion updatedPromotion = eventPromotionRepository.save(existingPromotion);
                        logger.info("Successfully updated promotion with ID: {}", updatedPromotion.getPromotionId());
                        return convertToResponseDTO(updatedPromotion);
                    })
                    .orElseThrow(() -> new EntityNotFoundException("Event Promotion with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during promotion update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during promotion update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during promotion update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating promotion: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) { 
        logger.info("Attempting to delete promotion with ID: {}", id);
        try {
            if (!eventPromotionRepository.existsById(id)) { 
                throw new EntityNotFoundException("Event Promotion with ID " + id + " not found.");
            }
            eventPromotionRepository.deleteById(id); 
            logger.info("Event Promotion with ID {} deleted successfully.", id);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during promotion deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during promotion deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting promotion: " + e.getMessage(), e);
        }
    }
}