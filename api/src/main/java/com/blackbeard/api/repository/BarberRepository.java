package com.blackbeard.api.repository;

import com.blackbeard.api.dto.BarberCredentialsDTO;
import com.blackbeard.api.dto.BarberDTO;
import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Barber;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.security.PublicKey;
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
        String sql = "INSERT INTO barber (name, password_hash) VALUES (?, ?) RETURNING *";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barber.getName());
            ps.setString(2, barber.getPasswordHash());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return barberResponse(rs);
            }
        } catch (SQLException e) {
            logger.error("Error saving barber: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving barber", e);
        }
        return null;
    }

    public Barber findById(int id) {
        String sql = "SELECT * FROM barber WHERE id = ?";
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
        String sql = "SELECT * FROM barber";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                barbers.add(barberResponse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching barbers: {}", e.getMessage(), e);
            throw new ApiException("Error fetching barbers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return barbers;
    }

    public Barber update(int id, Barber existingBarber) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE barber SET ");
        List<Object> params = new ArrayList<>();

        if (existingBarber.getName() != null) {
            sqlBuilder.append("name = ?, ");
            params.add(existingBarber.getName());
        }
        if (existingBarber.getPasswordHash() != null) {
            sqlBuilder.append("password_hash = ?, ");
            params.add(existingBarber.getPasswordHash());
        }

        sqlBuilder.append("updatedAt = current_timestamp WHERE id = ? RETURNING *");
        params.add(id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return barberResponse(rs);
            }
        } catch (SQLException e) {
            logger.error("Erro na atualização do barber: {}", e.getMessage(), e);
            throw new ApiException("Erro na atualização do barber", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }



    public boolean delete(int id) {
        String sql = "DELETE FROM barber WHERE id = ?";
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
        barber.setCreatedAt(rs.getTimestamp("createdAt"));
        barber.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return barber;
    }

    private Barber barberResponse(ResultSet rs) throws SQLException {
        Barber barber = new Barber();
        barber.setId(rs.getInt("id"));
        barber.setName(rs.getString("name"));
        barber.setCreatedAt(rs.getTimestamp("createdAt"));
        barber.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return barber;
    }

    public Barber findByName(String name){
        String sql = "SELECT * FROM barber WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractBarberFromResultSet(rs);
            } else {
                return null; 
            }
        } catch (SQLException e) {
            logger.error("Error finding barber: {}", e.getMessage(), e);
            throw new ApiException("Error SQL ao Buscar barbeiro.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
