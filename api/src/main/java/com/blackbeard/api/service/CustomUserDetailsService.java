package com.blackbeard.api.service;
import com.blackbeard.api.model.Barber;
import com.blackbeard.api.repository.BarberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final BarberRepository barberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(BarberRepository barberRepository, PasswordEncoder passwordEncoder) {
        this.barberRepository = barberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Barber barber = Optional.ofNullable(barberRepository.findByName(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new User(barber.getName(), barber.getPasswordHash(), Collections.emptyList());
    }
}
