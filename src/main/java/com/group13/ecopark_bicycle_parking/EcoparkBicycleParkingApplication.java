package com.group13.ecopark_bicycle_parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class EcoparkBicycleParkingApplication {
	public static void main(String[] args) {
		SpringApplication.run(EcoparkBicycleParkingApplication.class, args);
	}

}
