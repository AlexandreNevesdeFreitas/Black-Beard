package com.blackbeard.api.model;

import lombok.Data;

@Data
public class Client {
    private int id;
    private String nome;
    private String tel;
    private String createdAt;
    private String updatedAt;
}
