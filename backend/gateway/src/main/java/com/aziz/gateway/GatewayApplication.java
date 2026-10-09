package com.aziz.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GatewayApplication.class);
		app.run(args);
	}
}
