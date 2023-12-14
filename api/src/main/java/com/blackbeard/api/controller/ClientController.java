package com.blackbeard.api.controller;

import com.blackbeard.api.exception.ApiException;
import org.springframework.http.HttpStatus;

import com.blackbeard.api.dto.ClientDTO;
import com.blackbeard.api.model.Client;
import com.blackbeard.api.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        if (clientDTO.getName() == null || clientDTO.getTel() == null) {
            throw new ApiException("Requisição inválida: Nome e telefone são campos obrigatórios", HttpStatus.BAD_REQUEST);
        }
        Client client = new Client();
        client.setName(clientDTO.getName());
        client.setTel(clientDTO.getTel());

        Client savedClient = clientRepository.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @GetMapping
    public ResponseEntity<Client[]> findAllClients() {
        Client[] clients = clientRepository.findAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findClientById(@PathVariable int id) {
        Client client = clientRepository.findById(id);
        return ResponseEntity.ok(client);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Client> updateClientById(@Valid @PathVariable int id, @RequestBody ClientDTO clientDTO) {
        if (clientDTO.getName() == null && clientDTO.getTel() == null) {
            throw new ApiException("Sem campos válidos para serem atualizados", HttpStatus.BAD_REQUEST);
        }

        Client updatedClient = clientRepository.update(id, clientDTO.getName(), clientDTO.getTel());

        if (updatedClient != null) {
            return ResponseEntity.ok(updatedClient);
        } else {
            throw new ApiException("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable int id) {
        Boolean updatedClient = clientRepository.delete(id);

        if (!updatedClient) {
            throw new ApiException("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
