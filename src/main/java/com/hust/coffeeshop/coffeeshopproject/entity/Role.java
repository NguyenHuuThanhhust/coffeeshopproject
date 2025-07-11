package com.hust.coffeeshop.coffeeshopproject.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(name = "rolename")
    private String roleName;

}