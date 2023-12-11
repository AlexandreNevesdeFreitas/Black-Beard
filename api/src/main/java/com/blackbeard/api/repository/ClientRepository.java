package com.blackbeard.api.repository;

import com.blackbeard.api.controller.ClientController;
import com.blackbeard.api.model.Client;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "INSERT INTO clients (name, tel) VALUES (?, ?) RETURNING *";
        logger.info("Salvando cliente: {}", client);

        try {
            assert dataSource != null;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
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
                            client.setUpdatedAt(String.valueOf(rs.getTimestamp("updatedAt")));

                            logger.debug("Cliente inserido: ID={}, Nome={}, Telefone={}, Criado em={}",
                                    client.getId(), client.getName(), client.getTel(),
                                    client.getCreatedAt());
                        }
                    }
                } else {
                    logger.error("Nenhuma linha retornada após a inserção");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar cliente:", e);
        }

        return client;
    }

}