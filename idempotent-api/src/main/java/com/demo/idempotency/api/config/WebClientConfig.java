package com.demo.idempotency.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	@Value("${api-fake.url}")
	private String fakeApiUrl;

	@Bean
	WebClient webClient() {
		return WebClient.builder().baseUrl(fakeApiUrl).build();
	}

}
