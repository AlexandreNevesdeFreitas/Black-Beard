package com.blackbeard.api.service;

import com.blackbeard.api.dto.BarberDTO;
import com.blackbeard.api.dto.BarberResponseDTO;
import com.blackbeard.api.model.Barber;
import com.blackbeard.api.repository.BarberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.blackbeard.api.repository.ClientRepository.logger;

@Service
public class BarberService {
    private final BarberRepository barberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BarberService(BarberRepository barberRepository, PasswordEncoder passwordEncoder) {
        this.barberRepository = barberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public BarberResponseDTO createBarber(BarberDTO barberDTO) {
        logger.error(String.valueOf(barberDTO));
        Barber barber = new Barber();
        barber.setName(barberDTO.getName());
        barber.setPasswordHash(passwordEncoder.encode(barberDTO.getPassword()));
        Barber savedBarber = barberRepository.save(barber);
        return convertToBarberResponseDTO(savedBarber);
    }

    public List<BarberResponseDTO> getAllBarbers() {
        List<Barber> barbers = barberRepository.findAll();
        return barbers.stream()
                .map(this::convertToBarberResponseDTO)
                .collect(Collectors.toList());
    }

    public BarberResponseDTO getBarberById(int id) {
        Barber barber = barberRepository.findById(id);
        return convertToBarberResponseDTO(barber);
    }

    public BarberResponseDTO updateBarber(int id, BarberDTO barberDTO) {
        Barber existingBarber = barberRepository.findById(id);
        if (existingBarber != null) {
            boolean shouldUpdate = false;

            if (barberDTO.getName() != null && !barberDTO.getName().isEmpty()) {
                existingBarber.setName(barberDTO.getName());
                shouldUpdate = true;
            }
            if (barberDTO.getPassword() != null && !barberDTO.getPassword().isEmpty()) {
                existingBarber.setPasswordHash(passwordEncoder.encode(barberDTO.getPassword()));
                shouldUpdate = true;
            }

            if (shouldUpdate) {
                Barber updatedBarber = barberRepository.update(id, existingBarber);
                return convertToBarberResponseDTO(updatedBarber);
            }
        }
        return null;
    }

    public boolean deleteBarber(int id) {
        return barberRepository.delete(id);
    }

    private BarberResponseDTO convertToBarberResponseDTO(Barber barber) {
        BarberResponseDTO responseDTO = new BarberResponseDTO();
        responseDTO.setId(barber.getId());
        responseDTO.setName(barber.getName());
        responseDTO.setCreatedAt(barber.getCreatedAt());
        responseDTO.setUpdatedAt(barber.getUpdatedAt());
        return responseDTO;
    }

}
