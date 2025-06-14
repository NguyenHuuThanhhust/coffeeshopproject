package com.hust.coffeeshop.coffeeshopproject.repository;


import com.hust.coffeeshop.coffeeshopproject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}