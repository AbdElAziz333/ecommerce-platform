package com.aziz.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
public class OrderApplication {
	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OrderApplication.class);
        app.run(args);
	}
}