package com.blackbeard.api.repository;

import com.blackbeard.api.dto.BarberDTO;
import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Barber;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.blackbeard.api.repository.ClientRepository.logger;

@Repository
public class BarberRepository {
    private final DataSource dataSource;

    public BarberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Barber save(Barber barber) {
        String sql = "INSERT INTO barbers (name, password_hash) VALUES (?, ?) RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barber.getName());
            ps.setString(2, barber.getPasswordHash());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractBarberFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error saving barber: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving barber", e);
        }
        return null;
    }

    public Barber findById(int id) {
        String sql = "SELECT * FROM barbers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractBarberFromResultSet(rs);
            } else {
                throw new ApiException("Barber not found", HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            logger.error("Error finding barber: {}", e.getMessage(), e);
            throw new ApiException("Error finding barber", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Barber> findAll() {
        List<Barber> barbers = new ArrayList<>();
        String sql = "SELECT * FROM barbers";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                barbers.add(extractBarberFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching barbers: {}", e.getMessage(), e);
            throw new ApiException("Error fetching barbers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return barbers;
    }

    public Barber update(int id, BarberDTO updatedBarber) {
        String sql = "UPDATE barbers SET name = ?, email = ?, password_hash = ? WHERE id = ? RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updatedBarber.getName());
            ps.setString(3, updatedBarber.getPassword());
            ps.setInt(4, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractBarberFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error na atualização do barber: {}", e.getMessage(), e);
            throw new ApiException("Error updating barber", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }


    public boolean delete(int id) {
        String sql = "DELETE FROM barbers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error ao deletar barber: {}", e.getMessage(), e);
            throw new ApiException("Error deleting barber", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Barber extractBarberFromResultSet(ResultSet rs) throws SQLException {
        Barber barber = new Barber();
        barber.setId(rs.getInt("id"));
        barber.setName(rs.getString("name"));
        barber.setPasswordHash(rs.getString("password_hash"));
        barber.setCreatedAt(rs.getTimestamp("created_at"));
        barber.setUpdatedAt(rs.getTimestamp("updated_at"));
        return barber;
    }
}
