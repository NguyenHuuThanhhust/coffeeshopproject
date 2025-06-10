package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles") // Correct table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role { // Assuming you named this entity Roles, if Role, change class name

    @Id
    @Column(name = "rolename") // PK is varchar(50)
    private String roleName;

    // If Employees can have roles, you might have @ManyToMany here,
    // but since it's not in the provided schema diagrams for Employee,
    // we'll assume it's a standalone entity for now.
}