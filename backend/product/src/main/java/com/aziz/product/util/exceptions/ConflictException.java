package com.aziz.product.util.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}