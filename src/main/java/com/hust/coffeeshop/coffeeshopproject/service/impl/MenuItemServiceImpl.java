package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.repository.MenuItemRepository;
import com.hust.coffeeshop.coffeeshopproject.service.MenuItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MenuItemServiceImpl implements MenuItemService {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    private MenuItemResponseDTO convertToResponseDTO(MenuItem menuItem) {
        if (menuItem == null) return null;
        MenuItemResponseDTO dto = new MenuItemResponseDTO();
        dto.setMenuItemId(menuItem.getMenuItemId()); 
        dto.setItemName(menuItem.getItemName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice()); 
        dto.setCategory(menuItem.getCategory());
        dto.setStatus(menuItem.getStatus());
        dto.setIsAvailable(menuItem.getIsAvailable());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setMinSellingPrice(menuItem.getMinSellingPrice()); 
        return dto;
    }

    private MenuItem convertToEntity(MenuItemRequestDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(dto.getItemName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice()); 
        menuItem.setCategory(dto.getCategory());
        menuItem.setStatus(dto.getStatus() != null ? dto.getStatus() : "Available");
        menuItem.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        menuItem.setImageUrl(dto.getImageUrl());
        menuItem.setMinSellingPrice(dto.getMinSellingPrice()); 
        return menuItem;
    }

    @Override
    public List<MenuItemResponseDTO> getAllMenuItems() {
        logger.info("Fetching all menu items.");
        return menuItemRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MenuItemResponseDTO> getMenuItemById(Long id) { 
        logger.info("Fetching menu item with ID: {}", id);
        return menuItemRepository.findById(id).map(this::convertToResponseDTO);
    }

    @Override
    @Transactional
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO) {
        logger.info("Attempting to create menu item with DTO: {}", requestDTO);
        try {
            if (menuItemRepository.findByItemName(requestDTO.getItemName()).isPresent()) {
                throw new IllegalArgumentException("Menu item with name '" + requestDTO.getItemName() + "' already exists.");
            }
            MenuItem menuItem = convertToEntity(requestDTO);
            MenuItem savedMenuItem = menuItemRepository.save(menuItem);
            logger.info("Successfully created menu item with ID: {}", savedMenuItem.getMenuItemId());
            return convertToResponseDTO(savedMenuItem);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during menu item creation: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during menu item creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during menu item creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating menu item: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO) { 
        logger.info("Attempting to update menu item with ID: {}, DTO: {}", id, requestDTO);
        try {
            return menuItemRepository.findById(id).map(existingMenuItem -> { 
                existingMenuItem.setItemName(requestDTO.getItemName());
                existingMenuItem.setDescription(requestDTO.getDescription());
                existingMenuItem.setPrice(requestDTO.getPrice()); 
                existingMenuItem.setCategory(requestDTO.getCategory());
                existingMenuItem.setStatus(requestDTO.getStatus());
                existingMenuItem.setIsAvailable(requestDTO.getIsAvailable());
                existingMenuItem.setImageUrl(requestDTO.getImageUrl());
                existingMenuItem.setMinSellingPrice(requestDTO.getMinSellingPrice()); 

                MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
                logger.info("Successfully updated menu item with ID: {}", updatedMenuItem.getMenuItemId());
                return convertToResponseDTO(updatedMenuItem);
            }).orElseThrow(() -> new EntityNotFoundException("Menu Item with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during menu item update: {}", e.getMessage(), e);
            throw e;
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during menu item update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during menu item update: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating menu item: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) { 
        logger.info("Attempting to delete menu item with ID: {}", id);
        try {
            if (!menuItemRepository.existsById(id)) { 
                throw new EntityNotFoundException("Menu Item with ID " + id + " not found.");
            }
            menuItemRepository.deleteById(id); 
            logger.info("Menu Item with ID {} deleted successfully.", id);
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during menu item deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during menu item deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting menu item: " + e.getMessage(), e);
        }
    }
}