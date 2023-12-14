package com.blackbeard.api.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Client {
    private int id;
    private String name;
    private String tel;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
