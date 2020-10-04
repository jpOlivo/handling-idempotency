package com.demo.idempotency.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
@EnableScheduling
@EnableRetry
public class IdempotentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdempotentApiApplication.class, args);
	}

}
