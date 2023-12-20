package com.blackbeard.api.service;

import com.blackbeard.api.model.Barber;
import com.blackbeard.api.repository.BarberRepository;
import com.blackbeard.api.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthenticationService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public String authenticateBarber(String username, String password) {
        Barber barber = barberRepository.findByName(username);
        if (barber != null && passwordEncoder.matches(password, barber.getPasswordHash())) {
            return jwtUtils.generateToken(username);
        } else {
            throw new BadCredentialsException("Barber not found or password incorrect");
        }
    }

    public boolean isValidBarberCredentials(String username, String password) {
        Barber barber = barberRepository.findByName(username);
        if(barber != null){
            return passwordEncoder.matches(password, barber.getPasswordHash());
        } else {
            return false;
        }
    }
}

