package com.aziz.product.util.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateProductException extends ApiException {
    public DuplicateProductException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
