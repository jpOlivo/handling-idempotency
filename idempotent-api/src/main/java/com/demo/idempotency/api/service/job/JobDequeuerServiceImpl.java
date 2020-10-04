package com.demo.idempotency.api.service.job;

import static com.demo.idempotency.api.constant.Constants.BACKGROUND_JOBS_QUEUE;

import java.time.Duration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.demo.idempotency.api.dto.CbuDto;
import com.demo.idempotency.api.dto.StagedJobsDto;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class JobDequeuerServiceImpl implements JobDequeuerService {

	@Autowired
	private WebClient webClient;

	@RabbitListener(queues = BACKGROUND_JOBS_QUEUE)
	@Retryable(value = { ResponseStatusException.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
	public void dequeue(StagedJobsDto stagedJobsDto) {
		log.info("dequeuing job {} from {}", stagedJobsDto, BACKGROUND_JOBS_QUEUE);

		// performing foreign state mutation
		log.info("tx4 -> create cbu");
		this.webClient.post().uri("/cbuOnline")
				.header("Idempotency-Key", "api-".concat((String) stagedJobsDto.getJobArgs().get("idempotencyKey")))
				.body(BodyInserters.fromValue(stagedJobsDto))

				.retrieve().onStatus(HttpStatus::is4xxClientError, response -> {
					return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"4xx error on /cbuOnline external endpoint"));
				}).onStatus(HttpStatus::is5xxServerError, response -> {
					return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"5xx error on /cbuOnline external endpoint. Here could you retry the request ;)"));
				}).bodyToMono(CbuDto.class)
				// .timeout(Duration.ofSeconds(timeoutAccountDepositApi)) // for simulate
				// network issues
				.timeout(Duration.ofSeconds(5)) // for simulate network issues
				.block(); // TODO: to improvement!

		log.info("Cbu updated on external endpoint");

	}

}
