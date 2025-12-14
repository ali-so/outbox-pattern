package com.azki.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutboxPatternDemoApplication {

	static void main(String[] args) {
		SpringApplication.run(OutboxPatternDemoApplication.class, args);
	}

}
