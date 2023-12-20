package com.blackbeard.api.dto;


import lombok.Data;
import java.sql.Timestamp;

@Data
public class BarberResponseDTO {
    private int id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}