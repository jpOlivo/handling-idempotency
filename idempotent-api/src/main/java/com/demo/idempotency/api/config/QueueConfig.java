package com.demo.idempotency.api.config;

import static com.demo.idempotency.api.constant.Constants.BACKGROUND_JOBS_QUEUE;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
	@Bean
	public Queue jobsQueue() {
		return new Queue(BACKGROUND_JOBS_QUEUE, false);
	}
}
