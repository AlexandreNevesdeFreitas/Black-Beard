package com.blackbeard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BlackbeardApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackbeardApiApplication.class, args);
	}

}
