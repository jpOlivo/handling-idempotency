package com.demo.idempotency.api.action;

import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import com.demo.idempotency.api.enumeration.RecoveryPoint;
import com.demo.idempotency.api.model.IdempotencyKeys;
import com.demo.idempotency.api.repository.IdempotencyKeyRepository;
import com.demo.idempotency.api.util.BeanUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents an action to set a new recovery point on an @IdempotencyKeys
 */
@RequiredArgsConstructor
@Slf4j
public class RecoveryPointActionImpl implements Action {

	@NotNull
	private final String name;

	// @Autowired
	// private IdempotencyKeyRepository idempotencyKeyRepository;

	@Override
	public void execute(String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("key must be provided");
		}
		
		log.info("Setting {} recovery point for idempotency key {}", name, key);
		
		// TODO: use spring context with autowired
		IdempotencyKeyRepository idempotencyKeyRepository = BeanUtil.getBean(IdempotencyKeyRepository.class);

		log.debug("Getting IdempotencyKeys from DB for key {}", key);
		IdempotencyKeys idempotencyKeys = idempotencyKeyRepository.findByIdempotencyKey(key);

		// TODO: review it, duplicates with @RecoveryPointConverter#convertToEntityAttribute
		idempotencyKeys.setRecoveryPoint(Stream.of(RecoveryPoint.values()).filter(c -> c.getName().equals(name))
				.findFirst().orElseThrow(IllegalArgumentException::new));

		IdempotencyKeys idempotencyKeysUpdated = idempotencyKeyRepository.save(idempotencyKeys);
		log.debug("IdempotencyKeys updated in DB {}", idempotencyKeysUpdated);

		// log.info("Throw runtime exception");
		// throw new RuntimeException("test transactional");
	}

}
