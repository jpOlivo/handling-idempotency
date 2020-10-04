package com.demo.idempotency.api.action;

import org.springframework.http.HttpStatus;

import com.demo.idempotency.api.enumeration.RecoveryPoint;
import com.demo.idempotency.api.model.IdempotencyKeys;
import com.demo.idempotency.api.repository.IdempotencyKeyRepository;
import com.demo.idempotency.api.util.BeanUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents an action to set a new API response (which will be stored onto
 * an @IdempotencyKeys)
 */
@RequiredArgsConstructor
@Slf4j
public class ResponseActionImpl implements Action {
	private final HttpStatus status;
	private final String data; // TODO: persist data as JSON

	@Override
	public void execute(String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("key must be provided");
		}

		log.info("Setting response with http status {} for idempotency key {}", status, key);

		// TODO: use spring context with autowired
		IdempotencyKeyRepository idempotencyKeyRepository = BeanUtil.getBean(IdempotencyKeyRepository.class);

		log.debug("Getting IdempotencyKeys from DB for key {}", key);
		IdempotencyKeys idempotencyKey = idempotencyKeyRepository.findByIdempotencyKey(key);
		idempotencyKey.setLockedAt(null);
		idempotencyKey.setRecoveryPoint(RecoveryPoint.RECOVERY_POINT_FINISHED);
		idempotencyKey.setResponseCode(status.value());
		idempotencyKey.setResponseBody(data);

		IdempotencyKeys idempotencyKeysUpdated = idempotencyKeyRepository.save(idempotencyKey);
		log.debug("IdempotencyKeys updated in DB {}", idempotencyKeysUpdated);
	}

}
