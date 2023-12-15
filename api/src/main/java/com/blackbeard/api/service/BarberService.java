package com.blackbeard.api.service;

import com.blackbeard.api.dto.BarberDTO;
import com.blackbeard.api.model.Barber;
import com.blackbeard.api.repository.BarberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberService {
    private final BarberRepository barberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BarberService(BarberRepository barberRepository, PasswordEncoder passwordEncoder) {
        this.barberRepository = barberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Barber createBarber(BarberDTO barberDTO) {
        Barber barber = new Barber();
        barber.setName(barberDTO.getName());
        barber.setPasswordHash(passwordEncoder.encode(barberDTO.getPassword()));
        return barberRepository.save(barber);
    }

    public List<Barber> getAllBarbers() {
        return barberRepository.findAll();
    }

    public Barber getBarberById(int id) {
        return barberRepository.findById(id);
    }

    public Barber updateBarber(int id, BarberDTO barberDTO) {
        Barber existingBarber = barberRepository.findById(id);
        if (existingBarber != null) {
            existingBarber.setName(barberDTO.getName());
            // Somente atualize a senha se ela for fornecida no DTO
            if (barberDTO.getPassword() != null && !barberDTO.getPassword().isEmpty()) {
                existingBarber.setPasswordHash(passwordEncoder.encode(barberDTO.getPassword()));
            }
            return barberRepository.save(existingBarber);
        }
        return null; // ou lance uma exceção se preferir
    }

    public boolean deleteBarber(int id) {
        return barberRepository.delete(id);
    }

}
