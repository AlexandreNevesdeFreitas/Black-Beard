package com.blackbeard.api.dto;

public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    // Getter e Setter
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}