package com.hust.coffeeshop.coffeeshopproject.service; // Hoặc package service của bạn

import com.hust.coffeeshop.coffeeshopproject.entity.Ingredient;
import com.hust.coffeeshop.coffeeshopproject.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Ingredient createIngredient(Ingredient ingredient) {
        // Đây là nơi bạn sẽ đặt vai trò nếu bạn dùng cách ThreadLocal
        // Ví dụ: AuthRoleStatementInspector.setRoleForCurrentSession("Manager");
        try {
            // Đặt ngày nhập kho lần cuối nếu cần
            if (ingredient.getLastRestockDate() == null) {
                ingredient.setLastRestockDate(java.time.LocalDate.now());
            }
            return ingredientRepository.save(ingredient);
        } finally {
            // Và xóa vai trò sau khi hoàn thành
            // Ví dụ: AuthRoleStatementInspector.clearRoleForCurrentSession();
        }
    }
}