package com.aziz.product.util.exceptions;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}