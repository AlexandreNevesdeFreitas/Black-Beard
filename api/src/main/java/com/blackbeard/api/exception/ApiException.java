package com.blackbeard.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String errorDetails;

    public ApiException(String errorDetails, HttpStatus status) {
        super(errorDetails);
        this.status = status;
        this.errorDetails = errorDetails;
    }

}
