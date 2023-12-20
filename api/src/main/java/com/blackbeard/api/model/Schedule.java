package com.blackbeard.api.model;

import lombok.Data;

import java.security.PrivateKey;
import java.sql.Timestamp;

@Data
public class Schedule {
    private int id;
    private Timestamp appointment;
    private int clientId;
    private int barberId;
    private String service_type;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
