package com.demo.idempotency.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ErrorResponseConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().registerModules(new ProblemModule().withStackTraces(false),
				new ConstraintViolationProblemModule());
	}
}
