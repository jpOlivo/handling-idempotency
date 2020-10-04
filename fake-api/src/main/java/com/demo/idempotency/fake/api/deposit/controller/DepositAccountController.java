package com.demo.idempotency.fake.api.deposit.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.demo.idempotency.fake.api.deposit.dto.DepositAccountDto;
import com.demo.idempotency.fake.api.deposit.model.DepositAccount;
import com.demo.idempotency.fake.api.deposit.service.DepositService;

import lombok.extern.slf4j.Slf4j;

/**
 * This implementation do not support concurrent execution of idempotent
 * requests, it's only fake implementation. <br>
 * If you want to support concurrent execution of idempotent requests, probably
 * need some request management mechanism, to detect executing (yet incompleted)
 * requests and apply some sort of strategy to them:
 * <ul>
 * <li>wait until original finishes?</li>
 * <li>return an HTTP 307 redirect or a 409 conflict?</li>
 * <li>etc</li>
 * </ul>
 */
@RestController
@Slf4j
public class DepositAccountController {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private DepositService depositService;

	// @Value("${processDelay:0}")
	// private long processDelay;

	@PostMapping("/deposits")
	public ResponseEntity<DepositAccountDto> createDepositAccount(
			@Valid @RequestBody DepositAccountDto depositAccountDto, @RequestHeader("Idempotency-Key") String key) {
		log.info("[headers]: Idempotency-Key = {}", key);
		log.info("[body]: {}", depositAccountDto);

		// here implement the logic associated with the creation of a DepositAccount

		Cache cache = cacheManager.getCache("deposit_idempotency_cache");
		ValueWrapper valueWrapper = cache.get(key);

		if (valueWrapper != null) {
			// if the key is present, we return the cached results and do not execute
			// anything
			log.info("Returning results from IdempotencyKey Storage for key {}", key);
			return (ResponseEntity<DepositAccountDto>) valueWrapper.get();
		} else {
			// if the key is not present in our idempotency storage (Redis, Caffeine, etc.),
			// we perform
			// action and cache the output at our idempotency storage
			log.info("The key {} is not present in IdempotencyKey Storage", key);

			// Local mutation on resource
			log.info("Performing local mutation on DepositAccount resource");
			DepositAccount depositAccount = depositService.save(depositAccountDto);

			// build response
			depositAccountDto.setId(depositAccount.getId().toString());

			ResponseEntity<DepositAccountDto> responseEntity = new ResponseEntity<>(depositAccountDto,
					HttpStatus.CREATED);

			// update keys storage
			cache.put(key, responseEntity);
			log.info("Updating idempotency key storage, [{}:{}]", key, responseEntity);

			// log.info("Sleeping {} sec api service", processDelay);
			// Duration.ofSeconds(processDelay);

			return responseEntity;
		}

	}
}