package com.blackbeard.api.controller;

import com.blackbeard.api.dto.BarberDTO;
import com.blackbeard.api.model.Barber;
import com.blackbeard.api.repository.BarberRepository;
import com.blackbeard.api.service.BarberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/barbers")
public class BarberController {
    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @PostMapping
    public ResponseEntity<Barber> createBarber(@Valid @RequestBody BarberDTO barberDTO) {
        Barber newBarber = barberService.createBarber(barberDTO);
        return newBarber != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(newBarber)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    @GetMapping
    public ResponseEntity<List<Barber>> getAllBarbers() {
        List<Barber> barbers = barberService.getAllBarbers();
        return ResponseEntity.ok(barbers);
    }

    @GetMapping("/find")
    public ResponseEntity<Barber> getBarberById(@RequestParam int id) {
        Barber barber = barberService.getBarberById(id);
        if (barber != null) {
            return ResponseEntity.ok(barber);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/find")
    public ResponseEntity<Barber> updateBarber(@RequestParam int id, @Valid @RequestBody BarberDTO barberDTO) {
        Barber updatedBarber = barberService.updateBarber(id, barberDTO);
        if (updatedBarber != null) {
            return ResponseEntity.ok(updatedBarber);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/find")
    public ResponseEntity<Void> deleteBarber(@RequestParam int id) {
        boolean isDeleted = barberService.deleteBarber(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
