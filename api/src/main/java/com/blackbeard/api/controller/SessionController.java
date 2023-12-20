package com.blackbeard.api.controller;

import com.blackbeard.api.dto.BarberCredentialsDTO;
import com.blackbeard.api.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.blackbeard.api.repository.ClientRepository.logger;

@RestController
public class SessionController {
    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/session")
    public ResponseEntity<String> createSession(@RequestBody BarberCredentialsDTO barberCredentialsDTO) {

        if (!authenticationService.isValidBarberCredentials(barberCredentialsDTO.getUsername(), barberCredentialsDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authenticationService.authenticateBarber(barberCredentialsDTO.getUsername(), barberCredentialsDTO.getPassword());
        return ResponseEntity.ok(token);
    }


}