package com.blackbeard.api.model;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Schedule {
    private int id;
    private Timestamp appointment;
    private int clientId;
    private String service;
    private String note;  // Novo campo
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
