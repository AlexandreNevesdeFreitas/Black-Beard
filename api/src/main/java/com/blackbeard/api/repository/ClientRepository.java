package com.blackbeard.api.repository;

import com.blackbeard.api.controller.ClientController;
import com.blackbeard.api.model.Client;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    client.setId(rs.getInt("id"));
                    client.setName(rs.getString("name"));
                    client.setTel(rs.getString("tel"));
                    client.setCreatedAt(String.valueOf(rs.getTimestamp("createdAt")));
                    client.setUpdatedAt(String.valueOf(rs.getTimestamp("updatedAt")));
                    clientList.add(client);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar clientes:", e);
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
                            client.setId(rs.getInt("id"));
                            client.setName(rs.getString("name"));
                            client.setTel(rs.getString("tel"));
                            client.setCreatedAt(String.valueOf(rs.getTimestamp("createdAt")));
                            if (Objects.equals(String.valueOf(rs.getTimestamp("createdAt")), String.valueOf(rs.getTimestamp("updatedAt")))){
                                client.setUpdatedAt(null);
                            }else{
                                client.setUpdatedAt(String.valueOf(rs.getTimestamp("updatedAt")));
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Erro ao buscar cliente:", e);
            }
            return client;

        } catch (NumberFormatException e) {
            logger.error("ID fornecido não é um número inteiro válido", e);
            // Você pode retornar null ou lançar uma exceção personalizada aqui
            return null;
        }
    }

    public Client update(int id){
        try {
            String queryString = "UPDATE clients\n" +
                    "SET column1 = value1, column2 = value2, ...\n" +
                    "WHERE id = ?;";

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
                            client.setId(rs.getInt("id"));
                            client.setName(rs.getString("name"));
                            client.setTel(rs.getString("tel"));
                            client.setCreatedAt(String.valueOf(rs.getTimestamp("createdAt")));
                            if (Objects.equals(String.valueOf(rs.getTimestamp("createdAt")), String.valueOf(rs.getTimestamp("updatedAt")))){
                                client.setUpdatedAt(null);
                            }else{
                                client.setUpdatedAt(String.valueOf(rs.getTimestamp("updatedAt")));
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Erro ao buscar cliente:", e);
            }
            return client;

        } catch (NumberFormatException e) {
            logger.error("ID fornecido não é um número inteiro válido", e);
            // Você pode retornar null ou lançar uma exceção personalizada aqui
            return null;
        }
    }
}