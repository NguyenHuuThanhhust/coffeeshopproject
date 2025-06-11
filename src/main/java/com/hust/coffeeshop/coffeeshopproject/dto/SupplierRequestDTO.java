package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data; // Tự động tạo getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor; // Tự động tạo constructor không đối số
import lombok.AllArgsConstructor; // Tự động tạo constructor với tất cả các trường


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequestDTO {

    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
}