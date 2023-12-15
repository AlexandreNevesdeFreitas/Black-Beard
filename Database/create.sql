CREATE TABLE barbers (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) UNIQUE NOT NULL,
                         password_hash VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT now(),
                         updated_at TIMESTAMP DEFAULT NULL
);

CREATE TYPE service_type AS ENUM ('cabelo', 'barba', 'limpeza_de_pele', 'sobrancelha', 'pigmentacao');

CREATE TABLE
    schedules (
                  id SERIAL PRIMARY KEY,
                  datetime TIMESTAMP DEFAULT now(),
                  client_id INT,
                  service service_type NOT NULL,
                  FOREIGN KEY (client_id) REFERENCES clients(id),
                  createdat TIMESTAMP DEFAULT now(),
                  updatedat TIMESTAMP DEFAULT NULL
);

CREATE TABLE
    clients (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                tel VARCHAR(20) NOT NULL,
                createdat TIMESTAMP DEFAULT now(),
                updatedat TIMESTAMP DEFAULT NULL
    );