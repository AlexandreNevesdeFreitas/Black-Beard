package com.blackbeard.api.controller;

import com.blackbeard.api.dto.ScheduleDTO;
import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Client;
import com.blackbeard.api.model.Schedule;
import com.blackbeard.api.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private static final Logger logger = LogManager.getLogger(ScheduleController.class);
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setAppointment(scheduleDTO.getAppointment());
        schedule.setClientId(scheduleDTO.getClientId());
        schedule.setService_type(scheduleDTO.getService_type());
        schedule.setBarberId(scheduleDTO.getBarberId());
        schedule.setNote(scheduleDTO.getNote());


        try {
            Schedule savedSchedule = scheduleRepository.save(schedule);
            if (savedSchedule != null) {
                logger.debug("Schedule saved successfully: {}", savedSchedule);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedSchedule);
            } else {
                logger.error("Failed to save schedule." + savedSchedule);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            logger.error("Error while saving schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/find")
    public ResponseEntity<List<Schedule>> findAllAppointments(@RequestParam int barberId) {
        List<Schedule> schedules = scheduleRepository.findAllBarberAppointments(barberId);
        if (schedules.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/findOne")
    public ResponseEntity<Schedule> findAppointment(@RequestParam("id") int id, @RequestParam("barberId") int barberId) {
        Schedule schedules = scheduleRepository.findTheBarberAppointmentById(barberId, id);
        return ResponseEntity.ok(schedules);
    }

    @PatchMapping("/find")
    public ResponseEntity<Schedule> updateAppointment(@RequestParam int id, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        String service_type = scheduleDTO.getService_type();
        String note = scheduleDTO.getNote();
        Schedule scheduleUpdated = scheduleRepository.updateSchedule(id, service_type, note);

        if (scheduleUpdated == null) {
            return ResponseEntity.notFound().build(); // Ou outra resposta adequada
        }

        return ResponseEntity.ok(scheduleUpdated);
    }


    @DeleteMapping("/find")
    public ResponseEntity<Void> deleteAppointment(@RequestParam int id) {
        scheduleRepository.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

}
