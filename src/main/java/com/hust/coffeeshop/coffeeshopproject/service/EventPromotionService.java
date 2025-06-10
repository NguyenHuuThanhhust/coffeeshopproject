// src/main/java/com/hust/coffeeshop/coffeeshopproject/service/EventPromotionService.java
package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.EventPromotion;
import com.hust.coffeeshop.coffeeshopproject.repository.EventPromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventPromotionService {

    @Autowired
    private EventPromotionRepository eventPromotionRepository;

    // Helper method to convert Entity to Response DTO
    private EventPromotionResponseDTO convertToResponseDTO(EventPromotion promotion) {
        EventPromotionResponseDTO dto = new EventPromotionResponseDTO();
        dto.setPromotionId(promotion.getPromotionId());
        dto.setPromotionName(promotion.getPromotionName());
        dto.setDescription(promotion.getDescription());
        dto.setPromotionType(promotion.getPromotionType()); // Thêm vào
        dto.setValue(promotion.getValue());
        dto.setRemainingUses(promotion.getRemainingUses()); // Thêm vào
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setMinOrderAmount(promotion.getMinOrderAmount()); // Thêm vào
        // Trường 'status' đã bị loại bỏ
        return dto;
    }

    // Helper method to convert Request DTO to Entity (for new creation)
    private EventPromotion convertToEntity(EventPromotionRequestDTO dto) {
        EventPromotion promotion = new EventPromotion();
        // ID sẽ được tự động tạo bởi DB
        promotion.setPromotionName(dto.getPromotionName());
        promotion.setDescription(dto.getDescription());
        promotion.setPromotionType(dto.getPromotionType()); // Thêm vào
        promotion.setValue(dto.getValue());
        promotion.setRemainingUses(dto.getRemainingUses()); // Thêm vào
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setMinOrderAmount(dto.getMinOrderAmount()); // Thêm vào
        // Trường 'status' đã bị loại bỏ
        return promotion;
    }

    // CREATE
    public EventPromotionResponseDTO createPromotion(EventPromotionRequestDTO requestDTO) {
        EventPromotion promotion = convertToEntity(requestDTO);
        EventPromotion savedPromotion = eventPromotionRepository.save(promotion);
        return convertToResponseDTO(savedPromotion);
    }

    // READ ALL
    public List<EventPromotionResponseDTO> getAllPromotions() {
        return eventPromotionRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public Optional<EventPromotionResponseDTO> getPromotionById(Integer id) {
        return eventPromotionRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    // UPDATE
    public EventPromotionResponseDTO updatePromotion(Integer id, EventPromotionRequestDTO requestDTO) {
        return eventPromotionRepository.findById(id)
                .map(existingPromotion -> {
                    // Cập nhật các trường từ requestDTO
                    existingPromotion.setPromotionName(requestDTO.getPromotionName());
                    existingPromotion.setDescription(requestDTO.getDescription());
                    existingPromotion.setPromotionType(requestDTO.getPromotionType()); // Thêm vào
                    existingPromotion.setValue(requestDTO.getValue());
                    existingPromotion.setRemainingUses(requestDTO.getRemainingUses()); // Thêm vào
                    existingPromotion.setStartDate(requestDTO.getStartDate());
                    existingPromotion.setEndDate(requestDTO.getEndDate());
                    existingPromotion.setMinOrderAmount(requestDTO.getMinOrderAmount()); // Thêm vào
                    // Trường 'status' đã bị loại bỏ

                    EventPromotion updatedPromotion = eventPromotionRepository.save(existingPromotion);
                    return convertToResponseDTO(updatedPromotion);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event Promotion with ID " + id + " not found."));
    }

    // DELETE
    public void deletePromotion(Integer id) {
        if (!eventPromotionRepository.existsById(id)) {
            throw new EntityNotFoundException("Event Promotion with ID " + id + " not found.");
        }
        eventPromotionRepository.deleteById(id);
    }
}