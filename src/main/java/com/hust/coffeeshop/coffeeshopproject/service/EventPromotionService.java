package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.EventPromotion;
import com.hust.coffeeshop.coffeeshopproject.repository.EventPromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventPromotionService {

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

    @Transactional
    public EventPromotionResponseDTO createPromotion(EventPromotionRequestDTO requestDTO) {
        EventPromotion promotion = convertToEntity(requestDTO);
        EventPromotion savedPromotion = eventPromotionRepository.save(promotion);
        return convertToResponseDTO(savedPromotion);
    }

    public List<EventPromotionResponseDTO> getAllPromotions() {
        return eventPromotionRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventPromotionResponseDTO> getPromotionById(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        return eventPromotionRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    @Transactional
    public EventPromotionResponseDTO updatePromotion(Long id, EventPromotionRequestDTO requestDTO) { // SỬA TẠI ĐÂY: Integer -> Long
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
                    return convertToResponseDTO(updatedPromotion);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event Promotion with ID " + id + " not found."));
    }

    @Transactional
    public void deletePromotion(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        if (!eventPromotionRepository.existsById(id)) {
            throw new EntityNotFoundException("Event Promotion with ID " + id + " not found.");
        }
        eventPromotionRepository.deleteById(id);
    }
}