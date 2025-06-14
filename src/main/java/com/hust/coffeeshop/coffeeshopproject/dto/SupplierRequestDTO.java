package com.hust.coffeeshop.coffeeshopproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


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