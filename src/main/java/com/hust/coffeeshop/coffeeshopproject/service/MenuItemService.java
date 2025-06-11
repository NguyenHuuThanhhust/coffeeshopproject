package com.hust.coffeeshop.coffeeshopproject.service;

import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemRequestDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemResponseDTO; // MỚI
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.repository.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException; // Thêm import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.math.BigDecimal; // Thêm import này
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Thêm import này

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Helper methods to convert Entity <-> DTO
    private MenuItemResponseDTO convertToResponseDTO(MenuItem menuItem) {
        if (menuItem == null) return null;
        MenuItemResponseDTO dto = new MenuItemResponseDTO();
        dto.setMenuItemId(menuItem.getMenuItemId());
        dto.setItemName(menuItem.getItemName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice()); // Giả sử MenuItem.getPrice() đã trả về BigDecimal
        dto.setCategory(menuItem.getCategory());
        dto.setStatus(menuItem.getStatus());
        dto.setIsAvailable(menuItem.getIsAvailable());
        dto.setImageUrl(menuItem.getImageUrl());
        // dto.setThumbnail(menuItem.getThumbnail()); // Nếu thumbnail là byte[], không nên đưa vào DTO trả về trực tiếp
        dto.setMinSellingPrice(menuItem.getMinSellingPrice());
        return dto;
    }

    private MenuItem convertToEntity(MenuItemRequestDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(dto.getItemName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice()); // Giả sử MenuItem.getPrice() nhận BigDecimal
        menuItem.setCategory(dto.getCategory());
        menuItem.setStatus(dto.getStatus() != null ? dto.getStatus() : "Available");
        menuItem.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        menuItem.setImageUrl(dto.getImageUrl());
        // menuItem.setThumbnail(dto.getThumbnail());
        menuItem.setMinSellingPrice(dto.getMinSellingPrice());
        return menuItem;
    }

    public List<MenuItemResponseDTO> getAllMenuItems() { // SỬA KIEU TRA VE
        return menuItemRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<MenuItemResponseDTO> getMenuItemById(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        return menuItemRepository.findById(id).map(this::convertToResponseDTO); // BỎ Math.toIntExact(id)
    }

    @Transactional
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        MenuItem menuItem = convertToEntity(requestDTO);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToResponseDTO(savedMenuItem);
    }

    @Transactional
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO) { // SỬA TẠI ĐÂY
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setItemName(requestDTO.getItemName());
            existingMenuItem.setDescription(requestDTO.getDescription());
            existingMenuItem.setPrice(requestDTO.getPrice());
            existingMenuItem.setCategory(requestDTO.getCategory());
            existingMenuItem.setStatus(requestDTO.getStatus());
            existingMenuItem.setIsAvailable(requestDTO.getIsAvailable());
            existingMenuItem.setImageUrl(requestDTO.getImageUrl());
            // existingMenuItem.setThumbnail(requestDTO.getThumbnail());
            existingMenuItem.setMinSellingPrice(requestDTO.getMinSellingPrice());

            MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
            return convertToResponseDTO(updatedMenuItem);
        }).orElseThrow(() -> new EntityNotFoundException("Menu Item with ID " + id + " not found."));
    }

    @Transactional
    public void deleteMenuItem(Long id) { // SỬA TẠI ĐÂY: Integer -> Long
        if (!menuItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Menu Item with ID " + id + " not found.");
        }
        menuItemRepository.deleteById(id); // BỎ Math.toIntExact(id)
    }


}