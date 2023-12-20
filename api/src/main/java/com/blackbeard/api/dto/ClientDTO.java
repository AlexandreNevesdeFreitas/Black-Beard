package com.blackbeard.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class ClientDTO {
    @NotBlank(message = "O campo 'name' não pode estar em branco")
    private String name;

    @NotBlank(message = "O campo 'tel' não pode estar em branco")
    private String tel;

    @NotBlank(message = "O campo 'tel' não pode estar em branco")
    private int barberId;

}
