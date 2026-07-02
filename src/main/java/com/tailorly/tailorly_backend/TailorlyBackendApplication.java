package com.tailorly.tailorly_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TailorlyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TailorlyBackendApplication.class, args);
	}

}
