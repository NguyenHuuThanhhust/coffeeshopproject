package com.hust.coffeeshop.coffeeshopproject.service;


import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(Math.toIntExact(id));
    }

    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(Math.toIntExact(id));
    }

    // Ví dụ về một phương thức tìm kiếm tùy chỉnh:
    // public List<MenuItem> getMenuItemsByCategory(String category) {
    //     return menuItemRepository.findByCategory(category); // Phương thức này cần được định nghĩa trong MenuItemRepository
    // }
}