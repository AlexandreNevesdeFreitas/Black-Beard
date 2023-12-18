package com.blackbeard.api.controller;

import com.blackbeard.api.dto.ScheduleDTO;
import com.blackbeard.api.exception.ApiException;
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
        schedule.setService(scheduleDTO.getService());
        schedule.setNote(scheduleDTO.getNote());

        logger.debug("Created schedule object: {}", schedule);

        try {
            Schedule savedSchedule = scheduleRepository.save(schedule);
            if (savedSchedule != null) {
                logger.debug("Schedule saved successfully: {}", savedSchedule);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedSchedule);
            } else {
                logger.error("Failed to save schedule.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            logger.error("Error while saving schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/find")
    public ResponseEntity<Schedule> getScheduleById(@RequestParam int id) {
        Schedule schedule = scheduleRepository.findById(id);
        if (schedule != null) {
            return ResponseEntity.ok(schedule);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/allschedules")
    public ResponseEntity<List<Schedule>> getCurrentDayShedules(){
        List<Schedule> todaySchedules = scheduleRepository.findTodaySchedules();
        return ResponseEntity.ok(todaySchedules);
    }

    @PatchMapping("/find")
    public ResponseEntity<Schedule> updateSchedule(@RequestParam int id, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        Schedule updatedSchedule = scheduleRepository.update(id, scheduleDTO);
        if (updatedSchedule != null) {
            return ResponseEntity.ok(updatedSchedule);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/find")
    public ResponseEntity<Void> deleteSchedule(@RequestParam int id) {
        boolean isDeleted = scheduleRepository.delete(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
