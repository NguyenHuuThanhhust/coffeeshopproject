package com.hust.coffeeshop.coffeeshopproject.controller;

import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.MenuItemResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {


    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public List<MenuItemResponseDTO> getAllMenuItems() { // SỬA KIỂU TRẢ VỀ
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemById(@PathVariable Long id) { // SỬA KIỂU TRẢ VỀ
        return menuItemService.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(@RequestBody MenuItemRequestDTO requestDTO) { // SỬA TẠI ĐÂY (nhận RequestDTO)
        MenuItemResponseDTO savedMenuItem = menuItemService.createMenuItem(requestDTO); // SỬA TẠI ĐÂY (gọi createMenuItem và nhận DTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMenuItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemRequestDTO requestDTO) { // SỬA TẠI ĐÂY (nhận RequestDTO)
        MenuItemResponseDTO updatedMenuItem = menuItemService.updateMenuItem(id, requestDTO); // SỬA TẠI ĐÂY (gọi updateMenuItem và nhận DTO)
        return ResponseEntity.ok(updatedMenuItem);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        if (!menuItemService.getMenuItemById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}