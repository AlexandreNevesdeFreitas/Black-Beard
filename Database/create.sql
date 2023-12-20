-- Criação do tipo ENUM para os serviços
CREATE TYPE service_type AS ENUM ('cabelo', 'barba', 'limpeza_de_pele', 'sobrancelha', 'pigmentacao');

-- Criação da tabela barbers
CREATE TABLE barber (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) UNIQUE NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        createdAt TIMESTAMP DEFAULT now(),
                        updatedAt TIMESTAMP DEFAULT NULL
);

-- Criação da tabela clients com a relação para barbers
CREATE TABLE client (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        tel VARCHAR(20) NOT NULL,
                        barberId INT,
                        createdAt TIMESTAMP DEFAULT now(),
                        updatedAt TIMESTAMP DEFAULT NULL,
                        FOREIGN KEY (barberId) REFERENCES barber(id) ON DELETE SET NULL
);

-- Criação da tabela schedules

CREATE TABLE schedules (
                           id SERIAL PRIMARY KEY,
                           appointment TIMESTAMP NOT NULL,
                           clientId INT NOT NULL,
                           barberId INT NOT NULL,
                           service VARCHAR(15) NOT NULL,
                           note TEXT,
                           createdAt TIMESTAMP DEFAULT now(),
                           updatedAt TIMESTAMP DEFAULT NULL,
                           FOREIGN KEY (clientId) REFERENCES client(id),
                           FOREIGN KEY (barberId) REFERENCES barber(id) ON DELETE SET NULL
);