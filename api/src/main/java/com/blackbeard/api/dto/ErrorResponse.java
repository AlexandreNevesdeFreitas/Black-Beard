package com.blackbeard.api.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public void setError(String error) {
        this.error = error;
    }
}