// src/main/java/com/hust/coffeeshop/coffeeshopproject/exception/EntityNotFoundException.java
package com.hust.coffeeshop.coffeeshopproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus(HttpStatus.NOT_FOUND) là một annotation tiện lợi của Spring
// Nó sẽ tự động thiết lập mã trạng thái HTTP là 404 (Not Found)
// khi ngoại lệ này được ném ra từ một Controller.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    // Constructor mặc định
    public EntityNotFoundException() {
        super();
    }

    // Constructor với thông điệp
    public EntityNotFoundException(String message) {
        super(message);
    }

    // Constructor với thông điệp và nguyên nhân
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor với nguyên nhân
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}