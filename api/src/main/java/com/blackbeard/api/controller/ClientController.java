package com.blackbeard.api.controller;

import com.blackbeard.api.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackbeard.api.dto.ClientDTO;
import com.blackbeard.api.model.Client;
import com.blackbeard.api.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody ClientDTO clientDTO) {
        if (clientDTO.getName() == null || clientDTO.getTel() == null) {
            throw new ApiException(
                    "Requisição inválida",
                    HttpStatus.BAD_REQUEST
            );
        }
        Client client = new Client();
        client.setName(clientDTO.getName());
        client.setTel(clientDTO.getTel());

        Client savedClient = clientRepository.save(client);
        return ResponseEntity.ok(savedClient);
    }
}
