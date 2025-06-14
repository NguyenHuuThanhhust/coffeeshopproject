package com.hust.coffeeshop.coffeeshopproject.service;
import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemResponseDTO;
import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItemResponseDTO> getAllMenuItems();
    Optional<MenuItemResponseDTO> getMenuItemById(Long id);
    MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO);
    MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO);
    void deleteMenuItem(Long id);
}