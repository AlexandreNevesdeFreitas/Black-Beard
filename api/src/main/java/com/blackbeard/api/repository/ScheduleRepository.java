package com.blackbeard.api.repository;

import com.blackbeard.api.dto.ScheduleDTO;
import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Schedule;
import org.springframework.http.HttpStatus;
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
        String sql = "INSERT INTO schedules (appointment, clientId, barberId, service, note) VALUES (?, ?, ?, ?, ?) RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, schedule.getAppointment());
            ps.setInt(2, schedule.getClientId());
            ps.setInt(3, schedule.getBarberId());
            ps.setString(4, schedule.getService_type());
            ps.setString(5, schedule.getNote());

            logger.debug("Executing SQL query: {}", sql); // Log da consulta SQL
            logger.debug("Data being saved: appointment={}, clientId={}, barberId={}, service_type={}, note={}",
                    schedule.getAppointment(), schedule.getClientId(), schedule.getBarberId(), schedule.getService_type(), schedule.getNote());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error while saving schedule: {}", e.getMessage(), e);
        }
        return null;
    }

    public List<Schedule> findAllBarberAppointments(int barberId) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules WHERE barberId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, barberId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                schedules.add(extractScheduleFromResultSet(rs));
            }
        } catch (SQLException e) {
            // Log e tratamento de exceção
        }
        return schedules.isEmpty() ? null : schedules;
    }

    public Schedule findTheBarberAppointmentById(int barberId, int appointmentId) {
        String sql = "SELECT * FROM schedules WHERE barberId = ? AND id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, barberId);
            ps.setInt(2, appointmentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractScheduleFromResultSet(rs);
            }
        } catch (SQLException e) {
            // Tratamento de exceção
        }
        return null; // retorna null se nenhum agendamento for encontrado
    }

    public Schedule updateSchedule(int id, String service_type, String note) {
        logger.error(service_type);
        StringBuilder queryString = new StringBuilder("UPDATE schedules SET ");
        List<Object> params = new ArrayList<>();

        if (service_type != null) {
            queryString.append("service = ?, ");
            params.add(service_type);
        }
        if (note != null) {
            queryString.append("note = ?, ");
            params.add(note);
        }

        if (params.isEmpty()) {
            return null; 
        }

        // Removendo a última vírgula e espaço
        queryString = new StringBuilder(queryString.substring(0, queryString.length() - 2));

        queryString.append(" WHERE id = ?");
        params.add(id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryString.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return findById(id); // Assumindo que você tem um método para buscar Schedule por ID
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new ApiException("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);
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
        String sql = "UPDATE schedules SET appointment = ?, service = ?::service_type, barberId = ?, updatedat = now() WHERE id = ? RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, scheduleDTO.getAppointment());
            ps.setString(2, scheduleDTO.getService_type());
            ps.setInt(3, scheduleDTO.getBarberId());
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
        schedule.setClientId(rs.getInt("clientId"));
        schedule.setService_type(rs.getString("service"));
        schedule.setBarberId(rs.getInt("barberId"));
        schedule.setNote(rs.getString("note"));
        schedule.setCreatedAt(rs.getTimestamp("createdat"));
        schedule.setUpdatedAt(rs.getTimestamp("updatedat"));
        return schedule;
    }

    public void deleteSchedule(int id) {
        String sql = "DELETE FROM schedules WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Agendamento com ID " + id + " não foi encontrado e não pôde ser deletado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar agendamento: " + e.getMessage(), e);
        }
    }
}