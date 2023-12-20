package com.blackbeard.api.model;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Barber {
    private int id;
    private String name;
    private String passwordHash;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}