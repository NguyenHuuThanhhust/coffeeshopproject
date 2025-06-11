package com.hust.coffeeshop.coffeeshopproject.repository;

import com.hust.coffeeshop.coffeeshopproject.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> { // SỬA TẠI ĐÂY: Integer -> Long
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}