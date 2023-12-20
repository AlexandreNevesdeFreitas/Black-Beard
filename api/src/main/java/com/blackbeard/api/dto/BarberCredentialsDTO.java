package com.blackbeard.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BarberCredentialsDTO {
    @NotBlank(message = "O campo 'username' não pode estar em branco")
    private String username;

    @NotBlank(message = "O campo 'password' não pode estar em branco")
    private String password;

}
