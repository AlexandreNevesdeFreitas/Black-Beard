package com.blackbeard.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class ScheduleDTO {
    @NotNull(message = "A data e hora do agendamento não podem estar em branco.")
    private Timestamp appointment;

    @NotNull(message = "O id do cliente não pode estar em branco.")
    private int clientId;

    @NotBlank(message = "O serviço não pode estar em branco.")
    private String service;

    private String note;
}
