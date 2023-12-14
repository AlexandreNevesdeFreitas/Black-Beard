package com.blackbeard.api.repository;

import com.blackbeard.api.exception.ApiException;
import com.blackbeard.api.model.Client;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ClientRepository {
    private final DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(ClientRepository.class);

    public ClientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Client save(Client client) {
        String queryString = "INSERT INTO clients (name, tel) VALUES (?, ?) RETURNING *";
        try {
            assert dataSource != null;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(queryString)) {
                ps.setString(1, client.getName());
                ps.setString(2, client.getTel());

                boolean hasResultSet = ps.execute();
                if (hasResultSet) {
                    try (ResultSet rs = ps.getResultSet()) {
                        if (rs.next()) {
                            client.setId(rs.getInt("id"));
                            client.setName(rs.getString("name"));
                            client.setTel(rs.getString("tel"));
                            client.setCreatedAt(String.valueOf(rs.getTimestamp("createdAt")));
                            client.setUpdatedAt(null);
                        }
                    }
                } else {
                    logger.error("Nenhuma linha retornada após a inserção");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar cliente:::::::::::::::::::::::::::::", e);
        }
        return client;
    }

    public Client[] findAll() {
        String queryString = "SELECT * FROM clients";
        List<Client> clientList = new ArrayList<>();
        try {
            assert dataSource != null;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(queryString)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Client client = new Client();
                    setClient(client, rs);
                    clientList.add(client);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar cliente:::::::::::::::::::::::::::::", e);
        }
        return clientList.toArray(new Client[0]);
    }

    public Client findById(int id) {
        try {
            String queryString = "SELECT * FROM clients WHERE id = ?";
            assert dataSource != null;

            Client client = null;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(queryString)) {
                ps.setInt(1, id);
                boolean hasResultSet = ps.execute();
                if (hasResultSet) {
                    try (ResultSet rs = ps.getResultSet()) {
                        if (rs.next()) {
                            client = new Client();
                            setClient(client, rs);
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Erro ao buscar cliente:::::::::::::::::::::::::::::", e);
            }
            return client;
        } catch (NumberFormatException e) {
            logger.error("ID fornecido não é um número inteiro válido", e);
            return null;
        }
    }

    private void setClient(Client client, ResultSet rs) throws SQLException {
        client.setId(rs.getInt("id"));
        client.setName(rs.getString("name"));
        client.setTel(rs.getString("tel"));
        client.setCreatedAt(String.valueOf(rs.getTimestamp("createdAt")));
        client.setUpdatedAt(String.valueOf(rs.getTimestamp("updatedAt")));
    }

    public Client update(int id, String name, String tel) {
        String queryString = "UPDATE clients SET ";
        List<Object> params = new ArrayList<>();

        if (name != null) {
            queryString += "name = ?, ";
            params.add(name);
        }
        if (tel != null) {
            queryString += "tel = ?, ";
            params.add(tel);
        }

        if (params.isEmpty()) {
            return null;
        }

        queryString += "updatedAt = current_timestamp WHERE id = ?";
        params.add(id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryString.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return findById(id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar cliente:::::::::::::::::::::::::::::", e);
            throw new ApiException("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }


    public Boolean delete(int id) {
        try {
            String queryString = "DELETE FROM clients WHERE id = ?";
            assert dataSource != null;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(queryString)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                logger.error("Erro ao buscar cliente:::::::::::::::::::::::::::::", e);
                throw new ApiException("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            logger.error("ID fornecido não é um número inteiro válido", e);
            return false;
        }
    }
}
