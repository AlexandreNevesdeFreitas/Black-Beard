package com.blackbeard.api.repository;

import com.blackbeard.api.dto.ScheduleDTO;
import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Schedule;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public class ScheduleRepository {
    private static final Logger logger = LogManager.getLogger(ScheduleRepository.class);
    private final DataSource dataSource;

    public ScheduleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Schedule save(Schedule schedule) {
        String sql = "INSERT INTO schedules (appointment, client_id, service, note) VALUES (?, ?, ?::service_type, ?) RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, schedule.getAppointment());
            ps.setInt(2, schedule.getClientId());
            ps.setString(3, schedule.getService());
            ps.setString(4, schedule.getNote());

            logger.debug("Executing SQL query: {}", sql); // Log da consulta SQL
            logger.debug("Data being saved: appointment={}, client_id={}, service={}, note={}",
                    schedule.getAppointment(), schedule.getClientId(), schedule.getService(), schedule.getNote());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error while saving schedule: {}", e.getMessage()); // Log do erro
        }
        return null;
    }

    public List<Schedule> findAll() {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                schedules.add(extractScheduleFromResultSet(rs));
            }
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return schedules;
    }

    public Schedule findById(int id) {
        String sql = "SELECT * FROM schedules WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return null;
    }

    public Schedule update(int id, ScheduleDTO scheduleDTO) {
        String sql = "UPDATE schedules SET appointment = ?, service = ?::service_type, note = ?, updatedat = now() WHERE id = ? RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, scheduleDTO.getAppointment());
            ps.setString(2, scheduleDTO.getService());
            ps.setString(3, scheduleDTO.getNote());
            ps.setInt(4, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return false;
    }

    private Schedule extractScheduleFromResultSet(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setId(rs.getInt("id"));
        schedule.setAppointment(rs.getTimestamp("appointment"));
        schedule.setClientId(rs.getInt("client_id"));
        schedule.setService(rs.getString("service"));
        schedule.setNote(rs.getString("note"));
        schedule.setCreatedAt(rs.getTimestamp("created_at"));
        schedule.setUpdatedAt(rs.getTimestamp("updated_at"));
        return schedule;
    }

    public List<Schedule> findTodaySchedules(){
        List<Schedule> todaySchedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules WHERE DATE(appointment) = CURRENT_DATE;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                todaySchedules.add(extractScheduleFromResultSet(rs));
            }
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return todaySchedules;
    }
}