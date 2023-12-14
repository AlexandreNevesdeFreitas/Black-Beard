package com.blackbeard.api.config;

import com.blackbeard.api.repository.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class RepositoryConfig {

    @Bean
    public ClientRepository clientRepository(DataSource dataSource) {
        return new ClientRepository(dataSource);
    }
}
