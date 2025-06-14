package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.EventPromotionResponseDTO;
import java.util.List;
import java.util.Optional;

public interface EventPromotionService {
    EventPromotionResponseDTO createPromotion(EventPromotionRequestDTO requestDTO);
    List<EventPromotionResponseDTO> getAllPromotions();
    Optional<EventPromotionResponseDTO> getPromotionById(Long id); 
    EventPromotionResponseDTO updatePromotion(Long id, EventPromotionRequestDTO requestDTO); 
    void deletePromotion(Long id); 
}