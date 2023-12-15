package com.blackbeard.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class BarberDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
